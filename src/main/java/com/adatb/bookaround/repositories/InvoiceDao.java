package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Invoice;

public class InvoiceDao extends AbstractJpaDao<Invoice> {
    public InvoiceDao() { this.setEntityClass(Invoice.class); }
}
