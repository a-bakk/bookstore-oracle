package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Notification;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationDao extends AbstractJpaDao<Notification> {
    public NotificationDao() { this.setEntityClass(Notification.class); }
}
