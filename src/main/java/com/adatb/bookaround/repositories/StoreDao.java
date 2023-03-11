package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Store;
import org.springframework.stereotype.Repository;

@Repository
public class StoreDao extends AbstractJpaDao<Store> {
    public StoreDao() { this.setEntityClass(Store.class); }
}
