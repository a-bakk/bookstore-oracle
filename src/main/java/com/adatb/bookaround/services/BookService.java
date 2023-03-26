package com.adatb.bookaround.services;

import com.adatb.bookaround.entities.Book;
import com.adatb.bookaround.models.BookWithAuthorsAndGenres;
import com.adatb.bookaround.repositories.BookDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {

    private static final Logger logger = LogManager.getLogger(BookService.class);
    @Autowired
    private BookDao bookDao;

    /**
     * A könyveket listázza megjelenési idejük függvényében.
     * @return a könyvek kiegészítve írókkal és műfajokkal
     */
    public List<BookWithAuthorsAndGenres> getPopularBooks() {
        List<Book> books = bookDao.findBooksOrderedByPublicationDate();
        if (books == null) {
            logger.info("No books could be retrieved!");
            return new ArrayList<>();
        }
        return books.stream().map(book -> bookDao.encapsulateBook(book)).toList();
    }

}
