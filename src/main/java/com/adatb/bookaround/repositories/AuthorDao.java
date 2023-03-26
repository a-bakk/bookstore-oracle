package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Author;
import com.adatb.bookaround.entities.Book;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AuthorDao extends AbstractJpaDao<Author> {
    public AuthorDao() {this.setEntityClass(Author.class);}

    public List<Author> findByBook(Book book) {
        return entityManager.createQuery("SELECT a FROM Author a WHERE a.authorId.bookId = :bookId", Author.class)
                .setParameter("bookId", book.getBookId())
                .getResultList();
    }

}
