package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Notification;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NotificationDao extends AbstractJpaDao<Notification> {
    public NotificationDao() { this.setEntityClass(Notification.class); }

    public List<Notification> findByCustomerId(Long customerId) {
        return entityManager.createQuery("SELECT n FROM Notification n WHERE n.customer.customerId = :customerId",
                        Notification.class)
                .setParameter("customerId", customerId)
                .getResultList();
    }

}
