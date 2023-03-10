package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Book;

public class BookDao extends AbstractJpaDao<Book> {
    public BookDao() { this.setEntityClass(Book.class); }
}
