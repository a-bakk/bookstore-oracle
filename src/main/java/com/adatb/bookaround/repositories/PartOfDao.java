package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.PartOf;

public class PartOfDao extends AbstractJpaDao<PartOf> {
    public PartOfDao() { this.setEntityClass(PartOf.class); }
}
