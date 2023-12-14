package com.example.stockissue.facade;

import com.example.stockissue.serivce.OptimisticLockStockService;
import org.springframework.stereotype.Component;

@Component
public class OptimisticLockStockFacade {

    private final OptimisticLockStockService optimisticLockStockService;

    public OptimisticLockStockFacade(OptimisticLockStockService optimisticLockStockService) {
        this.optimisticLockStockService = optimisticLockStockService;
    }

    // Optimistic Lock 실패 시 재시도 하기 위한 용도
    public void decrease(Long id, Long quantity) throws InterruptedException {
        while (true) {
            try {
                optimisticLockStockService.decrease(id, quantity);

                // 정상적으로 업데이트가 된다면 while문을 빠져나옴
                break;
            } catch (Exception e) {
                // 수량 감소에 실패하면 50ms 후에 재시도
                Thread.sleep(50);
            }
        }
    }
}
