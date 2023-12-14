package com.example.stockissue.facade;

import com.example.stockissue.repository.LockRepository;
import com.example.stockissue.serivce.StockService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class NamedLockStockFacade {

    private final LockRepository lockRepository;

    private final StockService stockService;

    public NamedLockStockFacade(LockRepository lockRepository, StockService stockService) {
        this.lockRepository = lockRepository;
        this.stockService = stockService;
    }

    // 로직 전후로 락 획득, 해체
    @Transactional
    public void decrease(Long id, Long quantity) {
        try {
            // 락 획득
            lockRepository.getLock(id.toString());

            // 락 획득 후 재고 감소 로직 실행
            stockService.decrease(id, quantity);
        } finally {
            /**
             * 모든 로직이 종료되면 락 해제
             * 예외가 발생하면 락을 해제해 줘야함
             */
            lockRepository.releaseLock(id.toString());
        }
    }
}
