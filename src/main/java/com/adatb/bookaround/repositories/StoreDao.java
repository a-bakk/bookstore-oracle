package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.BusinessHours;
import com.adatb.bookaround.entities.Stock;
import com.adatb.bookaround.entities.Store;
import com.adatb.bookaround.models.BookWithAuthorsAndGenres;
import com.adatb.bookaround.models.StoreWithBusinessHours;
import com.adatb.bookaround.models.constants.StoreSize;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class StoreDao extends AbstractJpaDao<Store> {

    public StoreDao() {
        this.setEntityClass(Store.class);
    }

    public ArrayList<StoreWithBusinessHours> findAllStoresWithBusinessHours() {
        String jpql = "SELECT s, bh " +
                "FROM Store s " +
                "LEFT JOIN BusinessHours bh ON bh.store.storeId = s.storeId " +
                "ORDER BY bh.dayOfWeek ASC";

        List<Object[]> resultList = entityManager.createQuery(jpql, Object[].class)
                .getResultList();

        Map<Long, StoreWithBusinessHours> storesMap = new HashMap<>();

        for (Object[] result : resultList) {
            Store store = (Store) result[0];
            BusinessHours businessHours = (BusinessHours) result[1];

            StoreWithBusinessHours storeWithBusinessHours = storesMap.get(store.getStoreId());

            if (storeWithBusinessHours == null) {
                storeWithBusinessHours = new StoreWithBusinessHours(store, new ArrayList<>());
                storesMap.put(store.getStoreId(), storeWithBusinessHours);
            }

            if (businessHours != null) {
                storeWithBusinessHours.getBusinessHours().add(businessHours);
            }
        }
        return new ArrayList<>(storesMap.values());
    }

    public StoreWithBusinessHours findStoreWithBusinessHoursById(Long storeId) {
        String jpql = "SELECT s, bh " +
                "FROM Store s " +
                "LEFT JOIN BusinessHours bh ON bh.store.storeId = s.storeId " +
                "WHERE s.storeId = :storeId " +
                "ORDER BY bh.dayOfWeek ASC";

        List<Object[]> resultList = entityManager.createQuery(jpql, Object[].class)
                .setParameter("storeId", storeId)
                .getResultList();

        ArrayList<BusinessHours> listBusinessHours = new ArrayList<>();
        Store store = new Store();

        for (Object[] result : resultList) {
            BusinessHours businessHours = (BusinessHours) result[1];
            store = (Store) result[0];

            if (businessHours != null) {
                listBusinessHours.add(businessHours);
            }
        }
        return new StoreWithBusinessHours(store, listBusinessHours);
    }



    /**
     * [Összetett lekérdezés]
     *
     * @return minden bolthoz a raktáron levő könyvek
     */
    public Map<Long, Long> findNumberOfBooksForEachStore() {
        TypedQuery<Object[]> query = entityManager.createQuery("SELECT s.storeId, SUM(st.count) " +
                "FROM Store s " +
                "JOIN Stock st ON st.stockId.store.storeId = s.storeId " +
                "GROUP BY s.storeId", Object[].class);

        var results = query.getResultList();
        if (results.isEmpty())
            return null;

        return results.stream()
                .map(row -> new AbstractMap.SimpleEntry<Long, Long>
                        ((Long) row[0], (Long) row[1]))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * [Tárolt eljárás]
     *
     * @return az áruház mérete az előre definiált értékek alapján
     */
    public StoreSize findStoreSize(Long storeId) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("store_size")
                .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.OUT)
                .setParameter(1, storeId);

        query.execute();

        return StoreSize.valueOf((String) query.getOutputParameterValue(2));
    }

}
