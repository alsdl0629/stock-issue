package com.example.stockissue.facade;

import com.example.stockissue.repository.RedisLockRepository;
import com.example.stockissue.serivce.StockService;
import org.springframework.stereotype.Component;

@Component
public class LettuceLockStockFacade {

    private final RedisLockRepository redisLockRepository;

    private final StockService stockService;

    public LettuceLockStockFacade(RedisLockRepository redisLockRepository, StockService stockService) {
        this.redisLockRepository = redisLockRepository;
        this.stockService = stockService;
    }

    public void decrease(Long id, Long quantity) throws InterruptedException {
        while (!redisLockRepository.lock(id)) {
            /**
             * Lock 획득에 실패하면 Thread.sleep()을 사용해서 100ms 텀을 두고, 재시도 한다.
             * 레디스의 부하를 줄일 수 있다.
             */
            Thread.sleep(100);
        }

        try {
            stockService.decrease(id, quantity);
        } finally {
            redisLockRepository.unlock(id);
        }
    }
}
