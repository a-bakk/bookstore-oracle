package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Contains;

public class ContainsDao extends AbstractJpaDao<Contains> {
    public ContainsDao() { this.setEntityClass(Contains.class); }
}
