package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Book;
import com.adatb.bookaround.entities.Customer;
import com.adatb.bookaround.entities.PartOf;
import com.adatb.bookaround.entities.Wishlist;
import com.adatb.bookaround.models.WishlistWithContent;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

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

    /**
     * [Összetett lekérdezés]
     *
     * @return mely ügyfeleknek van a legtöbb könyv a kívánságlistájukon, az első 3
     */
    public Map<Customer, Long> findCustomersWithLargestWishlists() {
        TypedQuery<Object[]> query = entityManager.createQuery(
                "SELECT c, COUNT(p.partOfId.book.bookId) as number_of_items " +
                        "FROM Customer c " +
                        "JOIN Wishlist w ON c.customerId = w.customer.customerId " +
                        "JOIN PartOf p ON w.wishlistId = p.partOfId.wishlist.wishlistId " +
                        "GROUP BY c " +
                        "ORDER BY number_of_items DESC " +
                        "FETCH FIRST 3 ROWS ONLY", Object[].class);

        var results = query.getResultList();
        if (results.isEmpty())
            return null;

        return results.stream()
                .map(row -> new AbstractMap.SimpleEntry<Customer, Long>((Customer) row[0], (Long) row[1]))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
