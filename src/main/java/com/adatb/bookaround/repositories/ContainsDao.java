package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Contains;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ContainsDao extends AbstractJpaDao<Contains> {
    public ContainsDao() {
        this.setEntityClass(Contains.class);
    }

    public List<Contains> findByOrder(Long orderId) {
        return entityManager.createQuery("SELECT c " +
                        "FROM Contains c " +
                        "WHERE c.containsId.order.orderId = :orderId", Contains.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }
}
