package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Customer;

public class CustomerDao extends AbstractJpaDao<Customer> {
    public CustomerDao() { this.setEntityClass(Customer.class); }
}
