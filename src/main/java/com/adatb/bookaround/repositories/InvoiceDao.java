package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Invoice;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

@Repository
public class InvoiceDao extends AbstractJpaDao<Invoice> {
    public InvoiceDao() {
        this.setEntityClass(Invoice.class);
    }

    public Invoice findInvoiceByOrder(Long orderId) {
        TypedQuery<Invoice> query = entityManager.createQuery("SELECT i " +
                        "FROM Invoice i " +
                        "WHERE i.order.orderId = :orderId", Invoice.class)
                .setParameter("orderId", orderId);
        var results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
}
