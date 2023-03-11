package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Book;
import org.springframework.stereotype.Repository;

@Repository
public class BookDao extends AbstractJpaDao<Book> {
    public BookDao() { this.setEntityClass(Book.class); }
}
