package com.adatb.bookaround.services;

import com.adatb.bookaround.entities.Author;
import com.adatb.bookaround.entities.Book;
import com.adatb.bookaround.entities.Genre;
import com.adatb.bookaround.entities.compositepk.AuthorId;
import com.adatb.bookaround.entities.compositepk.GenreId;
import com.adatb.bookaround.models.BookWithAuthorsAndGenres;
import com.adatb.bookaround.repositories.AuthorDao;
import com.adatb.bookaround.repositories.BookDao;
import com.adatb.bookaround.repositories.GenreDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class BookService {

    private static final Logger logger = LogManager.getLogger(BookService.class);
    @Autowired
    private BookDao bookDao;

    @Autowired
    private AuthorDao authorDao;

    @Autowired
    private GenreDao genreDao;

    /**
     * A könyveket listázza megjelenési idejük függvényében.
     * @return a könyvek kiegészítve írókkal és műfajokkal
     */
    public List<BookWithAuthorsAndGenres> getLatestBooks() {
        List<Book> books = bookDao.findBooksOrderedByPublicationDate();
        if (books == null) {
            logger.warn("No books could be retrieved! (latest)");
            return new ArrayList<>();
        }
        return books.stream().map(book -> bookDao.encapsulateBook(book)).toList();
    }

    public List<BookWithAuthorsAndGenres> getPopularBooks() {
        List<Book> books = bookDao.findPopularBooksOrderedByOrderCount();
        if (books == null) {
            logger.warn("No books could be retrieved! (popularity)");
            return new ArrayList<>();
        }
        return books.stream().map(book -> bookDao.encapsulateBook(book)).toList();
    }

    public List<BookWithAuthorsAndGenres> getAllBookModels() {
        List<BookWithAuthorsAndGenres> books = bookDao.findAllBooksWithAuthorsAndGenres();
        if (books == null) {
            logger.warn("No books could be retrieved! (all)");
            return new ArrayList<>();
        }
        return books;
    }

    public boolean createBookWithAuthorsAndGenres(Book book, String authors, String genres) {
        String[] required = new String[] {
                book.getTitle(), book.getDescription(), book.getCover(),
                book.getWeight().toString(), book.getPrice().toString(),
                book.getNumberOfPages().toString(), book.getPublishedAt().toString(),
                book.getPublisher(), book.getIsbn(), book.getLanguage(),
                authors, genres
        };

        if (StoreService.checkForEmptyString(required))
            return false;

        String[] splitAuthors = authors.split(";");
        String[] splitGenres = genres.split(";");

        if (bookDao.create(book) == null)
            return false;

        for (String author : splitAuthors) {
            String[] splitName = author.split(" ");
            Author newAuthor = new Author(new AuthorId(
                    book.getBookId(),
                    splitName[0],
                    splitName[1]
            ));
            if (authorDao.create(newAuthor) == null)
                return false;
        }

        for (String genre : splitGenres) {
            Genre newGenre = new Genre(new GenreId(
               book.getBookId(),
               genre
            ));
            if (genreDao.create(newGenre) == null)
                return false;
        }
        return true;
    }



}
