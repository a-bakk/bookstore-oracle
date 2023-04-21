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

    public HashMap<BookWithAuthorsAndGenres, Integer> findStockForStoreById(Long storeId) {
        String jpql = "SELECT st " +
                "FROM Store s, Stock st " +
                "WHERE s.storeId = st.stockId.store.storeId AND s.storeId = :storeId";

        List<Object[]> resultList = entityManager.createQuery(jpql, Object[].class)
                .setParameter("storeId", storeId)
                .getResultList();

        HashMap<BookWithAuthorsAndGenres, Integer> stockForStore = new HashMap<>();

        for (Object[] obj : resultList) {
            Stock stock = (Stock) obj[0];

            if (stock != null && !stockForStore.containsKey(bookDao.encapsulateBook(stock.getStockId().getBook()))) {
                stockForStore.put(bookDao.encapsulateBook(stock.getStockId().getBook()), stock.getCount());
            }
        }

        return stockForStore;
    }
}
