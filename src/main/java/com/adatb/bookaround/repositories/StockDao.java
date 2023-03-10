package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Stock;

public class StockDao extends AbstractJpaDao<Stock> {
    public StockDao() { this.setEntityClass(Stock.class); }
}
