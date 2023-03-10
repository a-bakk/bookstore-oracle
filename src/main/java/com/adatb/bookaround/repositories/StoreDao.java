package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Store;

public class StoreDao extends AbstractJpaDao<Store> {
    public StoreDao() { this.setEntityClass(Store.class); }
}
