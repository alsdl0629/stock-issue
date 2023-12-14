package com.example.stockissue.facade;

import com.example.stockissue.domain.Stock;
import com.example.stockissue.repository.StockRepository;
import com.example.stockissue.serivce.PessimisticLockStockService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OptimisticLockStockFacadeTest {

    @Autowired
    private OptimisticLockStockFacade optimisticLockStockFacade;

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
                    optimisticLockStockFacade.decrease(1L, 1L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
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