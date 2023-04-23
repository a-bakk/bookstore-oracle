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

}
