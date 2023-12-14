package com.example.stockissue.serivce;

import com.example.stockissue.domain.Stock;
import com.example.stockissue.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {

    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    /**
     * 재고 감소 기능
     * NamedLockStockFacade에서 사용하기 위해 기존 거는 주석 처리
     */
////    @Transactional
//    public synchronized void decrease(Long id, Long quantity) {
//        // Stokc 조회
//        Stock stock = stockRepository.findById(id).orElseThrow();
//
//        // 재고 감소
//        stock.decrease(quantity);
//
//        // 갱신된 값 저장
//        stockRepository.saveAndFlush(stock);
//    }

    // NamedLockStockFacade의 트랜잭션과 별도로 실행되야 함
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void decrease(Long id, Long quantity) {
        // Stokc 조회
        Stock stock = stockRepository.findById(id).orElseThrow();

        // 재고 감소
        stock.decrease(quantity);

        // 갱신된 값 저장
        stockRepository.saveAndFlush(stock);
    }
}
