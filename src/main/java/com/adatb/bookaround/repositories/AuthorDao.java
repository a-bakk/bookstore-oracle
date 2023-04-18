package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Author;
import com.adatb.bookaround.entities.Book;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class AuthorDao extends AbstractJpaDao<Author> {
    public AuthorDao() {
        this.setEntityClass(Author.class);
    }

    public List<Author> findByBook(Book book) {
        return entityManager.createQuery("SELECT a FROM Author a WHERE a.authorId.bookId = :bookId", Author.class)
                .setParameter("bookId", book.getBookId())
                .getResultList();
    }

    /**
     * [Összetett lekérdezés]
     *
     * @return a legnépszerűbb író (rendelések alapján)
     */
    public Author findMostPopularAuthor() {
        TypedQuery<Author> query = entityManager.createQuery("SELECT a " +
                "FROM Contains c " +
                "JOIN Author a ON c.containsId.book.bookId = a.authorId.bookId " +
                "GROUP BY a " +
                "ORDER BY SUM(c.count) DESC", Author.class);
        var results = query.getResultList();
        if (results.isEmpty())
            return null;
        return results.get(0);
    }

    @Transactional
    public void delete(Long bookId, String firstName, String lastName) {
        Query query = entityManager.createQuery("DELETE FROM Author a " +
                        "WHERE a.authorId.bookId = :bookId AND " +
                        "a.authorId.firstName = :firstName AND " +
                        "a.authorId.lastName = :lastName")
                .setParameter("bookId", bookId)
                .setParameter("firstName", firstName)
                .setParameter("lastName", lastName);
        query.executeUpdate();
    }

    /**
     * [Összetett lekérdezés]
     *
     * @return az a 3 szerző, melyeknek a legdrágábbak a könyvei
     */
    public Map<String, String> findMostExpensiveAuthors() {
        TypedQuery<Object[]> query = entityManager.createQuery("SELECT a.authorId.firstName, a.authorId.lastName, AVG(b.price) " +
                "FROM Author a " +
                "JOIN Book b ON b.bookId = a.authorId.bookId " +
                "GROUP BY a.authorId.firstName, a.authorId.lastName " +
                "ORDER BY AVG(b.price) DESC " +
                "FETCH FIRST 3 ROWS ONLY", Object[].class);

        var results = query.getResultList();
        if (results.isEmpty())
            return null;

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setRoundingMode(RoundingMode.UP);

        return results.stream()
                .map(row -> new AbstractMap.SimpleEntry<String, String>
                        (row[0] + " " + row[1], decimalFormat.format(row[2])))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
