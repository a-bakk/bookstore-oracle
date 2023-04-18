package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Stock;
import com.adatb.bookaround.entities.compositepk.StockId;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StockDao extends AbstractJpaDao<Stock> {
    public StockDao() {
        this.setEntityClass(Stock.class);
    }

    public List<Stock> findStocksByBookId(Long bookId) {
        return entityManager.createQuery("SELECT s " +
                        "FROM Stock s " +
                        "WHERE s.stockId.book.bookId = :bookId", Stock.class)
                .setParameter("bookId", bookId)
                .getResultList();
    }

    @Transactional
    public void deleteByStockId(StockId stockId) {
        Query query = entityManager.createQuery("DELETE FROM Stock s " +
                        "WHERE s.stockId = :stockId")
                .setParameter("stockId", stockId);
        query.executeUpdate();
    }
}
