package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Invoice;
import org.springframework.stereotype.Repository;

@Repository
public class InvoiceDao extends AbstractJpaDao<Invoice> {
    public InvoiceDao() { this.setEntityClass(Invoice.class); }
}
