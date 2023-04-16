package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Author;
import com.adatb.bookaround.entities.Book;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    public int delete(Long bookId, String firstName, String lastName) {
        Query query = entityManager.createQuery("DELETE FROM Author a " +
                        "WHERE a.authorId.bookId = :bookId AND " +
                        "a.authorId.firstName = :firstName AND " +
                        "a.authorId.lastName = :lastName")
                .setParameter("bookId", bookId)
                .setParameter("firstName", firstName)
                .setParameter("lastName", lastName);
        return query.executeUpdate();
    }

}
