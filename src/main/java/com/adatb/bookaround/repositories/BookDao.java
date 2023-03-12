package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Author;
import com.adatb.bookaround.entities.Book;
import com.adatb.bookaround.entities.Genre;
import com.adatb.bookaround.models.BookWithAuthorsAndGenres;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class BookDao extends AbstractJpaDao<Book> {

    private static final Logger logger = LogManager.getLogger(BookDao.class);
    public BookDao() { this.setEntityClass(Book.class); }

    public List<BookWithAuthorsAndGenres> findAllBooksWithAuthorsAndGenres() {
        String jpql = "SELECT b, a, g " +
                "FROM Book b " +
                "LEFT JOIN Author a ON b.bookId = a.authorId.bookId " +
                "LEFT JOIN Genre g ON b.bookId = g.genreId.bookId";

        List<Object[]> resultList = entityManager.createQuery(jpql, Object[].class)
                .getResultList();

        Map<Long, BookWithAuthorsAndGenres> booksMap = new HashMap<>();

        for (Object[] result : resultList) {
            Book book = (Book) result[0];
            Author author = (Author) result[1];
            Genre genre = (Genre) result[2];

            BookWithAuthorsAndGenres bookWithAuthorsAndGenres = booksMap.get(book.getBookId());

            if (bookWithAuthorsAndGenres == null) {
                bookWithAuthorsAndGenres = new BookWithAuthorsAndGenres(book, new HashSet<>(), new HashSet<>());
                booksMap.put(book.getBookId(), bookWithAuthorsAndGenres);
            }

            if (author != null) {
                bookWithAuthorsAndGenres.getAuthors().add(author);
            }

            if (genre != null) {
                bookWithAuthorsAndGenres.getGenres().add(genre);
            }
        }

        return new ArrayList<>(booksMap.values());
    }
}
