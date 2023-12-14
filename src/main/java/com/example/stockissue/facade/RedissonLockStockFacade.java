package com.example.stockissue.facade;

import com.example.stockissue.serivce.StockService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedissonLockStockFacade {

    // redisson 관련 기능을 제공해주기 때문에 Lettuce처럼 직접 로직을 작성하지 않아도 된다.
    private final RedissonClient redissonClient;

    private final StockService stockService;

    public RedissonLockStockFacade(RedissonClient redissonClient, StockService stockService) {
        this.redissonClient = redissonClient;
        this.stockService = stockService;
    }

    public void decrease(Long id, Long quantity) {
        RLock lock = redissonClient.getLock(id.toString());


        try {
            // 몇초동안 락 획득을 시도할 것인지, 몇초동안 점유할 것인지 설정
            boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS); // 락 얻을 때까지10초 대기, 락 얻고 1초 유지

            // 락 획득 실패 시 로그를 남기고 종료
            if (!available) {
                System.out.println("lock 획득 실패");
                return;
            }

            stockService.decrease(id, quantity);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}
