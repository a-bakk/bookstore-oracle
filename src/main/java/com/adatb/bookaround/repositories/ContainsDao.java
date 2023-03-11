package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Contains;
import org.springframework.stereotype.Repository;

@Repository
public class ContainsDao extends AbstractJpaDao<Contains> {
    public ContainsDao() { this.setEntityClass(Contains.class); }
}
