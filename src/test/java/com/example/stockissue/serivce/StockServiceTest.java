package com.example.stockissue.serivce;

import com.example.stockissue.domain.Stock;
import com.example.stockissue.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class StockServiceTest {

    @Autowired
    private PessimisticLockStockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    void setUp() {
        stockRepository.saveAndFlush(new Stock(1L, 100L));
    }

    @AfterEach
    public void after() {
        stockRepository.deleteAll();
    }

    /**
     * 요청이 동시에 여러 개가 들어오면 어떻게 될까?
     */
    @Test
    void 재고감소() {
        long stockId = 1L;
        stockService.decrease(stockId, 1L);

        Stock stock = stockRepository.findById(stockId).orElseThrow();

        assertEquals(99, stock.getQuantity());
    }

    @Test
    void 동시에_100개의_요청() throws InterruptedException {
        int threadCount = 100;
        // ExecutorService는 비동기로 실행하는 작업을 단순화하여 사용할 수 있게 도와주는 Java API
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        /**
         * 100개의 요청이 모두 끝날 때까지 기다려야 하므로 CountDownLatch 활용
         * CountDownLatch는 다른 쓰레드에서 수행 중인 작업이 완료될 때까지 대기할 수 있도록 도와주는 클래스
         */
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < 100; i++) {
            executorService.submit(() -> {
                try {
                    stockService.decrease(1L, 1L);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow();

        assertEquals(0, stock.getQuantity());
    }
}