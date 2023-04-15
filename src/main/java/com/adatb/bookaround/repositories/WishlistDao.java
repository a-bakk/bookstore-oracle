package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Book;
import com.adatb.bookaround.entities.PartOf;
import com.adatb.bookaround.entities.Wishlist;
import com.adatb.bookaround.models.WishlistWithContent;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class WishlistDao extends AbstractJpaDao<Wishlist> {
    public WishlistDao() {
        this.setEntityClass(Wishlist.class);
    }

    public List<WishlistWithContent> findWishlistsForCustomer(Long customerId) {
        String jpql = "SELECT w, p " +
                "FROM Wishlist w " +
                "LEFT JOIN PartOf p ON w.wishlistId = p.partOfId.wishlist.wishlistId " +
                "WHERE w.customer.customerId = :customerId";

        List<Object[]> resultList = entityManager.createQuery(jpql, Object[].class)
                .setParameter("customerId", customerId)
                .getResultList();

        Map<Long, WishlistWithContent> wishlistWithContentMap = new HashMap<>();

        for (Object[] result : resultList) {
            Wishlist wishlist = (Wishlist) result[0];
            PartOf partOf = (PartOf) result[1];

            WishlistWithContent wishlistWithContent = wishlistWithContentMap.get(wishlist.getWishlistId());

            if (wishlistWithContent == null) {
                wishlistWithContent = new WishlistWithContent(wishlist, new ArrayList<>());
                wishlistWithContentMap.put(wishlist.getWishlistId(), wishlistWithContent);
            }

            if (partOf != null) {
                wishlistWithContent.getBooks().add(partOf.getPartOfId().getBook());
            }
        }

        return new ArrayList<>(wishlistWithContentMap.values());
    }

    public List<Book> findBooksByWishlistId(Long wishlistId) {
        return entityManager.createQuery("SELECT p " +
                        "FROM PartOf p " +
                        "WHERE p.partOfId.wishlist.wishlistId = :wishlistId", PartOf.class)
                .setParameter("wishlistId", wishlistId)
                .getResultList().stream().map(partOf -> partOf.getPartOfId().getBook()).toList();
    }

    public Integer findNumberOfWishlistsForCustomer(Long customerId) {
        var wishlists = findWishlistsForCustomer(customerId);
        return wishlists.isEmpty() ? 0 : wishlists.size();
    }
}
