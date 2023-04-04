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

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
            logger.warn("No book could be loaded with the following id: " + bookId + " (deletion)");
            return false;
        }
        bookDao.delete(bookId);
        return true;
    }

    public boolean modifyBookById(Long mBookId, String mTitle, String mDescription,
                                  String mCover, Double mWeight, Long mPrice,
                                  Integer mNumberOfPages, LocalDate mPublishedAt,
                                  String mPublisher, String mIsbn, String mLanguage,
                                  Long mDiscountedPrice, String mAuthors, String mGenres) {
        Book book = bookDao.find(mBookId);
        if (book == null) {
            logger.warn("No book could be loaded with the following id: " + mBookId + " (modification)");
            return false;
        }

        String[] required = new String[] {
                mTitle, mDescription, mCover,
                mWeight.toString(), mPrice.toString(),
                mNumberOfPages.toString(), mPublishedAt.toString(),
                mPublisher, mIsbn, mLanguage,
                mAuthors, mGenres
        };

        if (StoreService.checkForEmptyString(required))
            return false;

        // modify book first (equality check might not be needed, still safer)
        book.setTitle(Objects.equals(book.getTitle(), mTitle) ? book.getTitle() : mTitle);
        book.setDescription(Objects.equals(book.getDescription(), mDescription)
                ? book.getDescription() : mDescription);
        book.setCover(Objects.equals(book.getCover(), mCover) ? book.getCover() : mCover);
        book.setWeight(Objects.equals(book.getWeight(), mWeight) ? book.getWeight() : mWeight);
        book.setPrice(Objects.equals(book.getPrice(), mPrice) ? book.getPrice() : mPrice);
        book.setNumberOfPages(Objects.equals(book.getNumberOfPages(), mNumberOfPages)
                ? book.getNumberOfPages() : mNumberOfPages);
        book.setPublishedAt(Objects.equals(book.getPublishedAt(), mPublishedAt)
                ? book.getPublishedAt() : mPublishedAt);
        book.setPublisher(Objects.equals(book.getPublisher(), mPublisher) ? book.getPublisher() : mPublisher);
        book.setIsbn(Objects.equals(book.getIsbn(), mIsbn) ? book.getIsbn() : mIsbn);
        book.setLanguage(Objects.equals(book.getLanguage(), mLanguage) ? book.getLanguage() : mLanguage);
        book.setDiscountedPrice((mDiscountedPrice == null || mDiscountedPrice == 0)
                ? null : (Objects.equals(book.getDiscountedPrice(), mDiscountedPrice)
                ? book.getDiscountedPrice() : mDiscountedPrice));

        bookDao.update(book);

        // modify authors if required
        Set<Author> authors = new HashSet<>(authorDao.findByBook(book));
        String authorsAsString = BookService.joinStrings(authors.stream()
                .map(author -> author.getAuthorId().getFirstName()
                + " " + author.getAuthorId().getLastName()).collect(Collectors.toSet()));
        if (!Objects.equals(authorsAsString, mAuthors)) {
            Set<String> splitModifiedAuthors = Arrays.stream(mAuthors.split(";")).collect(Collectors.toSet());
            // remove the authors that already belong to the book
            for (Author author : authors) {
                for (String modifiedAuthor : splitModifiedAuthors) {
                    if (Objects.equals(author.getAuthorId().getFirstName() + " " + author.getAuthorId().getLastName(),
                    modifiedAuthor)) {
                        authors.remove(author);
                        splitModifiedAuthors.remove(modifiedAuthor);
                    }
                }
            }
            // if authors contains elements that the new values don't, delete them
            if (!authors.isEmpty()) {
                authors.forEach(author -> {
                    authorDao.delete(book.getBookId(),
                            author.getAuthorId().getFirstName(), author.getAuthorId().getLastName());
                });
            }
            // check if the new values contain authors that don't already exist, if so, create them
            if (!splitModifiedAuthors.isEmpty()) {
                splitModifiedAuthors.forEach(modifiedAuthor -> {
                    String[] splitName = modifiedAuthor.split(" ");
                    AuthorId authorId = new AuthorId(book.getBookId(), splitName[0], splitName[1]);
                    authorDao.create(new Author(authorId));
                });
            }
        }

        // modify genres if required
        // TODO
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
