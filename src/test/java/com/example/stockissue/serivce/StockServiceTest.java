package com.example.stockissue.serivce;

import com.example.stockissue.domain.Stock;
import com.example.stockissue.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class StockServiceTest {

    @Autowired
    private StockService stockService;

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
    public void 재고감소() {
        long stockId = 1L;
        stockService.decrease(stockId, 1L);

        Stock stock = stockRepository.findById(stockId).orElseThrow();

        assertEquals(99, stock.getQuantity());
    }
}