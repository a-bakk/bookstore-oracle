package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Wishlist;
import org.springframework.stereotype.Repository;

@Repository
public class WishlistDao extends AbstractJpaDao<Wishlist> {
    public WishlistDao() { this.setEntityClass(Wishlist.class); }
}
