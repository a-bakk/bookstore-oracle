package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Author;
import com.adatb.bookaround.entities.Book;
import com.adatb.bookaround.entities.Genre;
import com.adatb.bookaround.models.BookWithAuthorsAndGenres;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class BookDao extends AbstractJpaDao<Book> {

    private static final Logger logger = LogManager.getLogger(BookDao.class);

    @Autowired
    private AuthorDao authorDao;
    @Autowired
    private GenreDao genreDao;

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

    public List<Book> findBooksBelowPrice(int price) {
        String jpql = "SELECT b " +
                "FROM Book b " +
                "WHERE b.price <= :price";

        return entityManager.createQuery(jpql, Book.class)
                .setParameter("price", price)
                .getResultList();
    }

    public List<Book> findBooksOrderedByPublicationDate() {
        String jpql = "SELECT b " +
                "FROM Book b " +
                "ORDER BY b.publishedAt";

        return entityManager.createQuery(jpql, Book.class).getResultList();
    }

    public List<Book> findBooksRecommendedByBook(Book book) {
        String jpql = "SELECT b2, COUNT(*) AS order_count " +
                "FROM Contains c1 " +
                "JOIN Contains c2 ON c1.containsId.order.orderId = c2.containsId.order.orderId AND " +
                "c1.containsId.book.bookId <> c2.containsId.book.bookId " +
                "JOIN Book b1 ON c1.containsId.book.bookId = b1.bookId " +
                "JOIN Book b2 ON c2.containsId.book.bookId = b2.bookId " +
                "GROUP BY b2 " +
                "ORDER BY order_count DESC " +
                "FETCH FIRST 3 ROWS ONLY";

        List<Object[]> resultList = entityManager.createQuery(jpql, Object[].class)
                .getResultList();

        return resultList.stream().map(result -> (Book) result[0]).toList();
    }

    public List<Book> findPopularBooksOrderedByOrderCount() {
        String jpql = "SELECT b, COUNT(*) AS order_count " +
                "FROM Book b " +
                "JOIN Contains c ON b.bookId = c.containsId.book.bookId " +
                "GROUP BY b " +
                "ORDER BY order_count DESC";

        List<Object[]> resultList = entityManager.createQuery(jpql, Object[].class)
                .getResultList();

        return resultList.stream().map(result -> (Book) result[0]).toList();
    }

    /**
     * A könyv kiegészítése a szerzőivel és műfajaival.
     * @param book melyik könyvhöz kell hozzárendelni az adatokat
     * @return modelbe csomagolt könyvadatok
     */
    public BookWithAuthorsAndGenres encapsulateBook(Book book) {
        BookWithAuthorsAndGenres res = new BookWithAuthorsAndGenres();
        res.setBook(book);

        authorDao.findByBook(book).forEach(author ->
                res.getAuthors().add(author));

        genreDao.findByBook(book).forEach(genre ->
                res.getGenres().add(genre));

        return res;
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
