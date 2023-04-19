package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.BusinessHours;
import com.adatb.bookaround.entities.Store;
import com.adatb.bookaround.models.StoreWithBusinessHours;
import com.adatb.bookaround.services.StoreService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class BusinessHoursDao extends AbstractJpaDao<BusinessHours> {

    @Autowired
    private StoreDao storeDao;

    private static final Logger logger = LogManager.getLogger(StoreService.class);
    public BusinessHoursDao() {
        this.setEntityClass(BusinessHours.class);
    }

    /*public List<BusinessHours> findBusinessHoursForStoreById(Long storeId) {
        String jpql = "SELECT bh " +
                "FROM BusinessHours bh " +
                "WHERE bh.store.id = " + storeId + " " +
                "ORDER BY bh.dayOfWeek DESC ";



        List<Object[]> resultList = entityManager.createQuery(jpql, Object[].class)
                .getResultList();

        List<BusinessHours> businessHoursList = new ArrayList<>();

        for (Object[] result : resultList) {
            BusinessHours businessHours = (BusinessHours) result[0];

            if (businessHours != null) {
                businessHoursList.add(businessHours);
            }
        }
        return new ArrayList<>(businessHoursList);
    }*/


    /*public Map<Store, List<BusinessHours>> findBusinessHoursForEachStore() {
        List<StoreWithBusinessHours> storeWithBusinessHoursList = storeDao.findAllStoresWithBusinessHours();

        Map<Store, List<BusinessHours>> resultMap = new HashMap<>();

        for (StoreWithBusinessHours result : storeWithBusinessHoursList) {
            Store store = result.getStore();

            List<BusinessHours> businessHours = this.findBusinessHoursForStoreById(store.getStoreId());

            for (BusinessHours businessHoursElement: businessHours) {
                if (resultMap.containsKey(store)) {
                    resultMap.get(store).add(businessHoursElement);
                } else {
                    resultMap.put(store, new ArrayList<>());

                    if (businessHoursElement != null) {
                        resultMap.get(store).add(businessHoursElement);
                    }
                }
            }
        }

        return resultMap;
    }*/

    /*public List<BusinessHours> findBusinessHoursForEachStore() {
        List<StoreWithBusinessHours> storeWithBusinessHoursList = storeDao.findAllStoresWithBusinessHours();

        List<BusinessHours> resultList = new ArrayList<>();

        for (StoreWithBusinessHours result : storeWithBusinessHoursList) {
            Store store = result.getStore();

            List<BusinessHours> businessHours = this.findBusinessHoursForStoreById(store.getStoreId());

            businessHours.addAll(result.getBusinessHours());
        }

        return resultList;
    }*/
}
