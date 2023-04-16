package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Book;
import com.adatb.bookaround.entities.Genre;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class GenreDao extends AbstractJpaDao<Genre> {

    public GenreDao() {
        this.setEntityClass(Genre.class);
    }

    public List<Genre> findByBook(Book book) {
        return entityManager.createQuery("SELECT g FROM Genre g WHERE g.genreId.bookId = :bookId", Genre.class)
                .setParameter("bookId", book.getBookId())
                .getResultList();
    }

    @Transactional
    public int delete(Long bookId, String genreName) {
        Query query = entityManager.createQuery("DELETE FROM Genre g " +
                        "WHERE g.genreId.bookId = :bookId AND " +
                        "g.genreId.genreName = :genreName")
                .setParameter("bookId", bookId)
                .setParameter("genreName", genreName);
        return query.executeUpdate();
    }

    /**
     * Feladat: Műfajok mellé kigyűjteni, hogy hány, az adott műfajba tartozó könyv található az
     * adatbázisban (triviális lekérdezéssel).
     *
     * @return Műfaj neve és a hozzá tartozó könyvek száma.
     */
    public Map<String, Long> findNumberOfBooksByGenre() {
        String jpql = "SELECT genre.genreId.genreName, COUNT(genre.genreId.genreName) as numberOfBooks " +
                "FROM Genre genre " +
                "GROUP BY genre.genreId.genreName";

        List<Object[]> resultList = entityManager.createQuery(jpql, Object[].class)
                .getResultList();

        Map<String, Long> resultsConverted = new HashMap<>();

        for (Object[] result : resultList) {
            String genreName = (String) result[0];
            Long numberOfBooks = (Long) result[1];
            resultsConverted.put(genreName, numberOfBooks);
        }

        return resultsConverted;
    }

    /**
     * [Összetett lekérdezés]
     */
    public List<Book> findPopularBooksByGenreOrderedByOrderCount(String genreName) {
        String jpql = "SELECT b, COUNT(*) AS order_count " +
                "FROM Book b " +
                "JOIN Genre g ON b.bookId = g.genreId.bookId " +
                "JOIN Contains c ON b.bookId = c.containsId.book.bookId " +
                "WHERE g.genreId.genreName = :genreName " +
                "GROUP BY b " +
                "ORDER BY order_count DESC " +
                "FETCH FIRST 3 ROWS ONLY";

        List<Object[]> resultList = entityManager.createQuery(jpql, Object[].class)
                .setParameter("genreName", genreName)
                .getResultList();

        return resultList.stream().map(result -> (Book) result[0]).toList();
    }

    /**
     * [Összetett lekérdezés]
     *
     * @return a legnépszerűbb műfaj rendelések alapján
     */
    public Genre findMostPopularGenre() {
        TypedQuery<Genre> query = entityManager.createQuery("SELECT g " +
                "FROM Contains c " +
                "JOIN Genre g ON c.containsId.book.bookId = g.genreId.bookId " +
                "GROUP BY g " +
                "ORDER BY SUM(c.count) DESC", Genre.class);
        var results = query.getResultList();
        if (results.isEmpty())
            return null;
        return results.get(0);
    }

    /**
     * [Összetett lekérdezés]
     *
     * @return a műfajonkénti átlagár
     */
    public Map<String, String> findAveragePricePerGenre() {
        TypedQuery<Object[]> query = entityManager.createQuery("SELECT g.genreId.genreName, AVG(b.price) " +
                "FROM Book b " +
                "JOIN Genre g ON g.genreId.bookId = b.bookId " +
                "GROUP BY g.genreId.genreName " +
                "ORDER BY AVG(b.price) DESC ", Object[].class);

        var results = query.getResultList();
        if (results.isEmpty())
            return null;

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setRoundingMode(RoundingMode.UP);

        return results.stream()
                .map(row -> new AbstractMap.SimpleEntry<String, String>((String) row[0], decimalFormat.format((Double) row[1])))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
