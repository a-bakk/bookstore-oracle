package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.BusinessHours;

public class BusinessHoursDao extends AbstractJpaDao<BusinessHours> {
    public BusinessHoursDao() { this.setEntityClass(BusinessHours.class); }
}
