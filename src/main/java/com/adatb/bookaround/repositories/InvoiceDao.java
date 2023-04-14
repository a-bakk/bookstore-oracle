package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Invoice;
import com.adatb.bookaround.entities.Order;
import org.springframework.stereotype.Repository;

@Repository
public class InvoiceDao extends AbstractJpaDao<Invoice> {
    public InvoiceDao() { this.setEntityClass(Invoice.class); }

    public Invoice findInvoiceByOrder(Long orderId) {
        return entityManager.createQuery("SELECT i " +
                        "FROM Invoice i " +
                        "WHERE i.order.orderId = :orderId", Invoice.class)
                .setParameter("orderId", orderId)
                .getSingleResult();
    }
}
