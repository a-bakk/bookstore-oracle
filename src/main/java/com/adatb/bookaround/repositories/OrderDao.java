package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Order;

public class OrderDao extends AbstractJpaDao<Order> {
    public OrderDao () { this.setEntityClass(Order.class); }
}
