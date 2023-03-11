package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Order;
import org.springframework.stereotype.Repository;

@Repository
public class OrderDao extends AbstractJpaDao<Order> {
    public OrderDao () { this.setEntityClass(Order.class); }
}
