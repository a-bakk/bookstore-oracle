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
import java.util.Set;

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

    public BookWithAuthorsAndGenres getBookWithAuthorsAndGenresById(Long bookId) {
        BookWithAuthorsAndGenres book = bookDao.findBookWithAuthorsAndGenresById(bookId);
        if (book == null) {
            logger.warn("No book could be loaded with the following id: " + bookId);
            return new BookWithAuthorsAndGenres();
        }
        return book;
    }

    public List<BookWithAuthorsAndGenres> getRecommendationsByBookId(Long bookId) {
        return bookDao.findBooksRecommendedByBook(bookDao.find(bookId))
                .stream().map(b -> bookDao.encapsulateBook(b)
        ).toList();
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

    public boolean deleteBookById(Long bookId) {
        Book book = bookDao.find(bookId);
        if (book == null) {
            logger.warn("No book could be loaded with the following id: " + bookId);
            return false;
        }
        bookDao.delete(bookId);
        return true;
    }

    public static String joinStrings(Set<String> strings) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : strings) {
            stringBuilder.append(str).append(";");
        }
        if (!stringBuilder.isEmpty()) {
            stringBuilder.setLength(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }



}
