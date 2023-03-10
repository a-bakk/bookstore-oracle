package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Notification;

public class NotificationDao extends AbstractJpaDao<Notification> {
    public NotificationDao() { this.setEntityClass(Notification.class); }
}
