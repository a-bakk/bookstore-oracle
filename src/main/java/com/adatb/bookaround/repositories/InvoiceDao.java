package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Invoice;
import com.adatb.bookaround.models.constants.BookstoreDate;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.sql.Date;

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

    /**
     * [Tárolt eljárás]
     *
     * @return az id alapján kapott számla az ügyfélhez tartozik-e
     */
    public boolean checkIfInvoiceBelongsToCustomer(Long invoiceId, Long customerId) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("invoice_belongs_to_customer")
                .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, Long.class, ParameterMode.OUT)
                .setParameter(1, invoiceId)
                .setParameter(2, customerId);

        query.execute();

        return (Long) query.getOutputParameterValue(3) == 1;
    }

    /**
     * [Tárolt eljárás]
     *
     * @return adott hónapra a bevétel
     */
    public Long findRevenueForMonth(BookstoreDate bookstoreDate) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("revenue_per_month")
                .registerStoredProcedureParameter(1, Date.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, Date.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, Long.class, ParameterMode.OUT)
                .setParameter(1, Date.valueOf(bookstoreDate.getStartDate()))
                .setParameter(2, Date.valueOf(bookstoreDate.getEndDate()));

        query.execute();

        return query.getOutputParameterValue(3) == null ? 0 : (Long) query.getOutputParameterValue(3);
    }
}
