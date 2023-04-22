package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.BusinessHours;
import com.adatb.bookaround.entities.Store;
import com.adatb.bookaround.services.StoreService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import javax.management.Query;
import java.util.List;


@Repository
public class BusinessHoursDao extends AbstractJpaDao<BusinessHours> {

    private static final Logger logger = LogManager.getLogger(StoreService.class);

    public BusinessHoursDao() {
        this.setEntityClass(BusinessHours.class);
    }

    /*public void createBusinessHours(Short dayOfWeek, String openingTime, String closingTime, Store store) {
        String jpql = "INSERT INTO BusinessHours (dayOfWeek, openingTime, closingTime, store) " +
                "SELECT :dayOfWeek, :openingTime, :closingTime, :store";

        //logger.warn(dayOfWeek);
        //logger.warn(openingTime);
        //logger.warn(closingTime);
        //logger.warn(store.getStoreId());
        //logger.warn(store.getName());
        entityManager.createQuery(jpql)
                .setParameter("dayOfWeek", dayOfWeek)
                .setParameter("openingTime", openingTime)
                .setParameter("closingTime", closingTime)
                .setParameter("store", store)
                .executeUpdate();
    }*/

    public Long getGreatestBusinessHoursId() {
        List<BusinessHours> businessHours = this.findAll();
        Long greatestIndex = 0L;
        for (BusinessHours businessHour : businessHours) {
            if (businessHour.getHoursId() > greatestIndex) {
                greatestIndex = businessHour.getHoursId();
            }
        }
        return greatestIndex;
    }
}
