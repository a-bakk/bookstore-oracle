package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.BusinessHours;
import com.adatb.bookaround.entities.Store;
import com.adatb.bookaround.models.StoreWithBusinessHours;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class StoreDao extends AbstractJpaDao<Store> {
    public StoreDao() { this.setEntityClass(Store.class); }

    public List<StoreWithBusinessHours> findAllStoresWithBusinessHours() {
        String jpql = "SELECT s, bh " +
                "FROM Store s " +
                "LEFT JOIN BusinessHours bh ON bh.store.storeId = s.storeId";

        List<Object[]> resultList = entityManager.createQuery(jpql, Object[].class)
                .getResultList();

        Map<Long, StoreWithBusinessHours> storesMap = new HashMap<>();

        for (Object[] result : resultList) {
            Store store = (Store) result[0];
            BusinessHours businessHours = (BusinessHours) result[1];

            StoreWithBusinessHours storeWithBusinessHours = storesMap.get(store.getStoreId());

            if (storeWithBusinessHours == null) {
                storeWithBusinessHours = new StoreWithBusinessHours(store, new HashSet<>());
                storesMap.put(store.getStoreId(), storeWithBusinessHours);
            }

            if (businessHours != null) {
                storeWithBusinessHours.getBusinessHours().add(businessHours);
            }
        }
        return new ArrayList<>(storesMap.values());
    }

}
