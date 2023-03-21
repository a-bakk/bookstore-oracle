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

    /**
     * Listázása a könyveknek a hozzájuk tartozó authorokkal és genrekkel,
     * a BookWithAuthorsAndGenres modelbe helyezéssel.
     * @return Az ezekből alkotott lista.
     */
    public List<BookWithAuthorsAndGenres> findAllBooksWithAuthorsAndGenres() {
        String jpql = "SELECT b, a, g " +
                "FROM Book b " +
                "LEFT JOIN Author a ON b.bookId = a.authorId.bookId " +
                "LEFT JOIN Genre g ON b.bookId = g.genreId.bookId";

        List<Object[]> resultList = entityManager.createQuery(jpql, Object[].class)
                .getResultList();

        return getEntitiesFromResultList(resultList);
    }

    /**
     * Filterezés szerző alapján.
     * @param author A könyv szerzője.
     * @return Egy lista, melyben a filterezésnek megfelelő BookWithAuthorsAndGenres példányok vannak.
     */
    public List<BookWithAuthorsAndGenres> filterBooksWithAuthorsAndGenres(Author author) {
        String jpql = "SELECT b, a, g " +
                "FROM Book b " +
                "LEFT JOIN Author a ON b.bookId = a.authorId.bookId " +
                "LEFT JOIN Genre g ON b.bookId = g.genreId.bookId " +
                "WHERE a.authorId.firstName = :firstName AND a.authorId.lastName = :lastName " +
                "ORDER BY b.bookId ASC";

        List<Object[]> resultList = entityManager.createQuery(jpql, Object[].class)
                .setParameter("firstName", author.getAuthorId().getFirstName())
                .setParameter("lastName", author.getAuthorId().getLastName())
                .getResultList();

        return getEntitiesFromResultList(resultList);
    }

    /**
     * Az előző overloadja, melyben címre filterezünk.
     * @param title A könyv címe, melyre keresni szeretnénk.
     * @return Egy lista, melyben a filterezésnek megfelelő BookWithAuthorsAndGenres példányok vannak.
     */
    public List<BookWithAuthorsAndGenres> filterBooksWithAuthorsAndGenres(String title) {
        String jpql = "SELECT b, a, g " +
                "FROM Book b " +
                "LEFT JOIN Author a ON b.bookId = a.authorId.bookId " +
                "LEFT JOIN Genre g ON b.bookId = g.genreId.bookId " +
                "WHERE b.title LIKE CONCAT('%', :title, '%') " +
                "ORDER BY b.bookId ASC";

        List<Object[]> resultList = entityManager.createQuery(jpql, Object[].class)
                .setParameter("title", title).getResultList();

        return getEntitiesFromResultList(resultList);
    }

    public List<BookWithAuthorsAndGenres> filterBooksWithAuthorsAndGenres(Genre genre) {
        String jpql = "SELECT b, a, g " +
                "FROM Book b " +
                "LEFT JOIN Author a ON b.bookId = a.authorId.bookId " +
                "LEFT JOIN Genre g ON b.bookId = g.genreId.bookId " +
                "WHERE g.genreId.genreName LIKE CONCAT('%', :genre, '%') " +
                "ORDER BY b.bookId ASC";

        List<Object[]> resultList = entityManager.createQuery(jpql, Object[].class)
                .setParameter("genre", genre.getGenreId().getGenreName()).getResultList();

        return getEntitiesFromResultList(resultList);
    }

    public List<BookWithAuthorsAndGenres> filterBooksWithAuthorsAndGenres(Author author, String title) {
        String jpql = "SELECT b, a, g " +
                "FROM Book b " +
                "LEFT JOIN Author a ON b.bookId = a.authorId.bookId " +
                "LEFT JOIN Genre g ON b.bookId = g.genreId.bookId " +
                "WHERE a.authorId.firstName = :firstName AND a.authorId.lastName = :lastName " +
                "AND b.title LIKE CONCAT('%', :title, '%') " +
                "ORDER BY b.bookId ASC";

        List<Object[]> resultList = entityManager.createQuery(jpql, Object[].class)
                .setParameter("firstName", author.getAuthorId().getFirstName())
                .setParameter("lastName", author.getAuthorId().getLastName())
                .setParameter("title", title)
                .getResultList();

        return getEntitiesFromResultList(resultList);
    }

    public List<BookWithAuthorsAndGenres> filterBooksWithAuthorsAndGenres(Author author, Genre genre) {
        String jpql = "SELECT b, a, g " +
                "FROM Book b " +
                "LEFT JOIN Author a ON b.bookId = a.authorId.bookId " +
                "LEFT JOIN Genre g ON b.bookId = g.genreId.bookId " +
                "WHERE a.authorId.firstName = :firstName AND a.authorId.lastName = :lastName " +
                "AND g.genreId.genreName LIKE CONCAT('%', :genre, '%') " +
                "ORDER BY b.bookId ASC";

        List<Object[]> resultList = entityManager.createQuery(jpql, Object[].class)
                .setParameter("firstName", author.getAuthorId().getFirstName())
                .setParameter("lastName", author.getAuthorId().getLastName())
                .setParameter("genre", genre.getGenreId().getGenreName())
                .getResultList();

        return getEntitiesFromResultList(resultList);
    }

    public List<BookWithAuthorsAndGenres> filterBooksWithAuthorsAndGenres(String title, Genre genre) {
        String jpql = "SELECT b, a, g " +
                "FROM Book b " +
                "LEFT JOIN Author a ON b.bookId = a.authorId.bookId " +
                "LEFT JOIN Genre g ON b.bookId = g.genreId.bookId " +
                "WHERE b.title LIKE CONCAT('%', :title, '%') " +
                "AND g.genreId.genreName LIKE CONCAT('%', :genre, '%') " +
                "ORDER BY b.bookId ASC";

        List<Object[]> resultList = entityManager.createQuery(jpql, Object[].class)
                .setParameter("title", title)
                .setParameter("genre", genre.getGenreId().getGenreName())
                .getResultList();

        return getEntitiesFromResultList(resultList);
    }

    public List<BookWithAuthorsAndGenres> filterBooksWithAuthorsAndGenres(Author author, String title, Genre genre) {
        String jpql = "SELECT b, a, g " +
                "FROM Book b " +
                "LEFT JOIN Author a ON b.bookId = a.authorId.bookId " +
                "LEFT JOIN Genre g ON b.bookId = g.genreId.bookId " +
                "WHERE a.authorId.firstName = :firstName AND a.authorId.lastName = :lastName " +
                "AND b.title LIKE CONCAT('%', :title, '%')  " +
                "AND g.genreId.genreName LIKE CONCAT('%', :genre, '%') " +
                "ORDER BY b.bookId ASC";

        List<Object[]> resultList = entityManager.createQuery(jpql, Object[].class)
                .setParameter("firstName", author.getAuthorId().getFirstName())
                .setParameter("lastName", author.getAuthorId().getLastName())
                .setParameter("title", title)
                .setParameter("genre", genre.getGenreId().getGenreName())
                .getResultList();

        return getEntitiesFromResultList(resultList);
    }

    /**
     * A könyvek listázása a megjelenési idejük függvényében.
     * @return A megfelelő BookWithAuthorsAndGenres entitásokat tartalmazó lista.
     */
    public List<BookWithAuthorsAndGenres> findAllBooksWithAuthorsAndGenresOrderedByReleaseDate() {
        String jpql = "SELECT b, a, g " +
                "FROM Book b " +
                "LEFT JOIN Author a ON b.bookId = a.authorId.bookId " +
                "LEFT JOIN Genre g ON b.bookId = g.genreId.bookId " +
                "ORDER BY b.publishedAt DESC";

        List<Object[]> resultList = entityManager.createQuery(jpql, Object[].class)
                .getResultList();

        return getEntitiesFromResultList(resultList);
    }

    /**
     * A lekérés eredményének átmappolása az entityknek megfelelően.
     * @param resultList Általános objektum lista.
     * @return Egy olyan lista, melyben már BookWithAuthorsAndGenres példányok vannak.
     */
    private List<BookWithAuthorsAndGenres> getEntitiesFromResultList(List<Object[]> resultList) {
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
