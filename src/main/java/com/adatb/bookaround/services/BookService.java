package com.adatb.bookaround.services;

import com.adatb.bookaround.entities.Author;
import com.adatb.bookaround.entities.Book;
import com.adatb.bookaround.entities.Genre;
import com.adatb.bookaround.entities.compositepk.AuthorId;
import com.adatb.bookaround.entities.compositepk.GenreId;
import com.adatb.bookaround.models.BookWithAuthorsAndGenres;
import com.adatb.bookaround.models.constants.OnStockStatus;
import com.adatb.bookaround.repositories.AuthorDao;
import com.adatb.bookaround.repositories.BookDao;
import com.adatb.bookaround.repositories.GenreDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
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

    /**
     * A könyveket listázza megjelenési idejük függvényében.
     *
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

    public List<BookWithAuthorsAndGenres> getBestsellersByGenre(String genreName) {
        var result = genreDao.findPopularBooksByGenreOrderedByOrderCount(genreName);
        if (result == null || result.isEmpty()) {
            logger.warn("No books could be loaded for genre: " + genreName);
            return null;
        }
        return result.stream().map(b -> bookDao.encapsulateBook(b)).toList();
    }

    public List<BookWithAuthorsAndGenres> getPopularBooks() {
        List<Book> books = bookDao.findPopularBooksOrderedByOrderCount();
        if (books == null) {
            logger.warn("No books could be retrieved! (popularity)");
            return new ArrayList<>();
        }
        return books.stream().map(book -> bookDao.encapsulateBook(book)).toList();
    }

    @SuppressWarnings("unused")
    public List<BookWithAuthorsAndGenres> getAllBookModels() {
        List<BookWithAuthorsAndGenres> books = bookDao.findAllBooksWithAuthorsAndGenres();
        if (books == null) {
            logger.warn("No books could be retrieved! (all)");
            return new ArrayList<>();
        }
        return books;
    }

    public Map<String, Long> getGenreListAndNumberOfBooksPerGenre() {
        var result = genreDao.findNumberOfBooksByGenre();
        if (result == null || result.isEmpty()) {
            logger.warn("Genre names and number of books per genre could not be loaded!");
            return new HashMap<>();
        }
        return result;
    }

    public BookWithAuthorsAndGenres getEncapsulatedBook(Long bookId) {
        return bookDao.encapsulateBook(bookDao.find(bookId));
    }

    public BookWithAuthorsAndGenres getBookWithAuthorsAndGenresById(Long bookId) {
        BookWithAuthorsAndGenres book = bookDao.findBookWithAuthorsAndGenresById(bookId);
        if (book == null) {
            logger.warn("No book could be loaded with the following id: " + bookId);
            return new BookWithAuthorsAndGenres();
        }
        OnStockStatus stockStatus = bookDao.findStockStatusForBook(book.getBook().getBookId());
        book.setOnStockStatus(stockStatus);
        return book;
    }

    public List<BookWithAuthorsAndGenres> getRecommendationsByBookId(Long bookId) {
        return bookDao.findBooksRecommendedByBook(bookDao.find(bookId))
                .stream().map(b -> bookDao.encapsulateBook(b)
                ).toList();
    }

    public boolean createBookWithAuthorsAndGenres(Book book, String authors, String genres) {
        String[] required = new String[]{
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

    /**
     * A könyvek módosítását elvégző metódus. Paraméterekben kapja az új értékeket, ezeknek megfelelően végzi el
     * a módosításokat. Amennyiben egy érték nem változott, a régi értéke marad az adatbázisban. Az írók és
     * műfajok módosítása a következőképpen működik: amennyiben az új értékek megegyeznek a régivel, nem történik semmi;
     * ha új, az adatbázisban még nem létező értékek vagy elvault értékek vannak, ennek megfelelően új entitások lesznek
     * létrehozva vagy törölve, tehát nem valódi update végződik el. Ez betudható annak, hogy mindkét entitás esetében
     * a teljes entitások csak (összetett) kulcsból állnak, nehéz lenne a meglévőket módosítani/nem is lehetne.
     * Utólagos észrevétel: annak az ellenőrzésére, hogy mely írók/műfajok léteznek már egy olyan megoldás lett
     * alkalmazva, hogy az egyezőket eltávolítottam mindkét kollekcióból. Ez generált olyan problémákat, hogy
     * a kollekción való átiterálás közben remove()-oltam elementet, melyet nem lehet. Ezért lett végül iterátor és
     * ConcurrentSkipListSet használva, mely ezt képes kezelni exception dobása nélkül, viszont mindkét entitynek
     * Comparable-nek kell lennie. Valószínüleg egyszerűbb lett volna két új kollekcióhoz hozzáadni a nem megegyezőket
     * és ennek megfelelően létrehozni/törölni, nem kellett volna hozzá ConcurrentSkipListSet, Iterator meg a Comparable
     * interfész implementációja.
     *
     * @return a művelet sikeressége, csak akkor true, ha a könyvet, az írókat és a műfajokat is frissíteni lehetett, ha
     * az utóbbi kettő szükséges volt
     */
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

        String[] required = new String[]{
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
        // ConcurrentSkipListSet is used so that items can be removed while iterating through the set
        Set<Author> authors = new ConcurrentSkipListSet<>(authorDao.findByBook(book));
        String authorsAsString = BookService.joinStrings(authors.stream()
                .map(author -> author.getAuthorId().getFirstName()
                        + " " + author.getAuthorId().getLastName())
                .collect(Collectors.toCollection(ConcurrentSkipListSet::new)));
        if (!Objects.equals(authorsAsString, mAuthors)) {
            Set<String> splitModifiedAuthors = Arrays.stream(mAuthors.split(";"))
                    .collect(Collectors.toCollection(ConcurrentSkipListSet::new));
            // remove the authors that already belong to the book
            Iterator<Author> authorIterator = authors.iterator();
            while (authorIterator.hasNext()) {
                Author author = authorIterator.next();
                Iterator<String> splitModifiedAuthorsIterator = splitModifiedAuthors.iterator();
                while (splitModifiedAuthorsIterator.hasNext()) {
                    String modifiedAuthor = splitModifiedAuthorsIterator.next();
                    if (Objects.equals(author.getAuthorId().getFirstName() + " " + author.getAuthorId().getLastName(),
                            modifiedAuthor)) {
                        authors.remove(author);
                        splitModifiedAuthors.remove(modifiedAuthor);
                    }
                }
            }

            // if authors contain elements that the new values don't, delete them
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
        Set<Genre> genres = new ConcurrentSkipListSet<>(genreDao.findByBook(book));
        String genresAsString = BookService.joinStrings(genres.stream()
                .map(genre -> genre.getGenreId().getGenreName())
                .collect(Collectors.toCollection(ConcurrentSkipListSet::new)));
        if (!Objects.equals(genresAsString, mGenres)) {
            Set<String> splitModifiedGenres = Arrays.stream(mGenres.split(";"))
                    .collect(Collectors.toCollection(ConcurrentSkipListSet::new));
            // remove the genres that already belong to the book
            Iterator<Genre> genreIterator = genres.iterator(); // iterators required because we are removing elements
            // while iterating through them
            while (genreIterator.hasNext()) {
                Genre genre = genreIterator.next();
                Iterator<String> splitModifiedGenresIterator = splitModifiedGenres.iterator();
                while (splitModifiedGenresIterator.hasNext()) {
                    String modifiedGenre = splitModifiedGenresIterator.next();
                    if (Objects.equals(genre.getGenreId().getGenreName(), modifiedGenre)) {
                        genres.remove(genre);
                        splitModifiedGenres.remove(modifiedGenre);
                    }
                }
            }

            // if genres contain elements that the new values don't, delete them
            if (!genres.isEmpty()) {
                genres.forEach(genre -> {
                    genreDao.delete(book.getBookId(), genre.getGenreId().getGenreName());
                });
            }

            // if the new values contain genres that don't already exist, create them
            if (!splitModifiedGenres.isEmpty()) {
                splitModifiedGenres.forEach(modifiedGenre -> {
                    GenreId genreId = new GenreId(book.getBookId(), modifiedGenre);
                    genreDao.create(new Genre(genreId));
                });
            }
        }

        // modifications should be done
        return true;
    }

}
