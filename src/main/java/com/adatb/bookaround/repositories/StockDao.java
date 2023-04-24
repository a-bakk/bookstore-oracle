package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Stock;
import com.adatb.bookaround.entities.Store;
import com.adatb.bookaround.entities.compositepk.StockId;
import com.adatb.bookaround.models.BookWithAuthorsAndGenres;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class StockDao extends AbstractJpaDao<Stock> {
    @Autowired
    private BookDao bookDao;

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

    @Transactional
    public boolean deleteStockFromStore(Long storeId, Long bookId) {
        Query query = entityManager.createQuery("DELETE FROM Stock s " +
                        "WHERE s.stockId.store.storeId = :storeId " +
                        "AND s.stockId.book.bookId = :bookId")
                .setParameter("storeId", storeId)
                .setParameter("bookId", bookId);
        query.executeUpdate();

        return true;
    }

    @Transactional
    public void updateByStockId(StockId stockId, Integer updateCount) {
        Integer recentCount = entityManager.createQuery("SELECT s.count " +
                                        "FROM Stock s " +
                                        "WHERE s.stockId = :stockId", Integer.class)
                .setParameter("stockId", stockId)
                .getResultList().get(0);

        Query query = entityManager.createQuery("UPDATE Stock s " +
                        "SET s.count = :updateCount " +
                        "WHERE s.stockId = :stockId")
                .setParameter("updateCount", updateCount + recentCount)
                .setParameter("stockId", stockId);
        query.executeUpdate();
    }

    public StockId findStockId(Long bookId, Long storeId) {
        List<Stock> stockList = entityManager.createQuery("SELECT s " +
                        "FROM Stock s " +
                        "WHERE s.stockId.book.bookId = :bookId AND s.stockId.store.storeId = :storeId", Stock.class)
                .setParameter("bookId", bookId)
                .setParameter("storeId", storeId)
                .getResultList();

        return stockList.get(0).getStockId();
    }

    public Map<Store, List<BookWithAuthorsAndGenres>> findStockForEachStore() {
        //SELECT s.name, st.book_id, st.count
        //FROM STORE s, STOCK st
        //WHERE s.store_id = st.store_id
        String jpql = "SELECT s, st " +
                "FROM Store s, Stock st " +
                "WHERE s.storeId = st.stockId.store.storeId";

        List<Object[]> resultList = entityManager.createQuery(jpql, Object[].class)
                .getResultList();

        Map<Store, List<BookWithAuthorsAndGenres>> result = new HashMap<>();

        for (Object[] obj : resultList) {
            Store store = (Store) obj[0];
            Stock stock = (Stock) obj[1];

            BookWithAuthorsAndGenres bookWithAuthorsAndGenres = null;
            if (stock != null) {
                bookWithAuthorsAndGenres = bookDao.encapsulateBook(stock.getStockId().getBook());
            }

            if (result.containsKey(store)) {
                result.get(store).add(bookWithAuthorsAndGenres);
            } else {
                result.put(store, new ArrayList<>());

                if (bookWithAuthorsAndGenres != null) {
                    result.get(store).add(bookWithAuthorsAndGenres);
                }

            }
        }

        return result;
    }

    public List<Stock> findStockForStoreById(Long storeId) {
        String jpql = "SELECT st " +
                "FROM Store s, Stock st " +
                "WHERE s.storeId = st.stockId.store.storeId AND s.storeId = :storeId";

        List<Object[]> resultList = entityManager.createQuery(jpql, Object[].class)
                .setParameter("storeId", storeId)
                .getResultList();

        List<Stock> stockForStore = new ArrayList<>();

        for (Object[] obj : resultList) {
            Stock stock = (Stock) obj[0];

            if (stock != null && !stockForStore.contains(stock)) {
                stockForStore.add(stock);
            }
        }

        return stockForStore;
    }

    /*public boolean updateStock(StockId stockId, Integer count) {
        String jpql = "UPDATE Stock SET count = :count WHERE stockId = :stockId";

        List resultList = entityManager.createQuery(jpql)
                .setParameter("count", count)
                .setParameter("stockId", stockId)
                .getResultList();

        return true;
    }*/
}
