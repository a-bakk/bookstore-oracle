package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Book;
import com.adatb.bookaround.entities.PartOf;
import com.adatb.bookaround.entities.Wishlist;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
public class PartOfDao extends AbstractJpaDao<PartOf> {
    public PartOfDao() {
        this.setEntityClass(PartOf.class);
    }

    public PartOf findByBookAndWishlist(Book book, Wishlist wishlist) {
        TypedQuery<PartOf> query = entityManager.createQuery("SELECT p " +
                        "FROM PartOf p " +
                        "WHERE p.partOfId.wishlist = :wishlist AND " +
                        "p.partOfId.book = :book", PartOf.class)
                .setParameter("book", book)
                .setParameter("wishlist", wishlist);
        var results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Transactional
    public int delete(Book book, Wishlist wishlist) {
        Query query = entityManager.createQuery("DELETE FROM PartOf p " +
                        "WHERE p.partOfId.book = :book AND " +
                        "p.partOfId.wishlist = :wishlist")
                .setParameter("book", book)
                .setParameter("wishlist", wishlist);

        return query.executeUpdate();
    }
}
