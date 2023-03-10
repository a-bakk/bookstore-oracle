package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Wishlist;

public class WishlistDao extends AbstractJpaDao<Wishlist> {
    public WishlistDao() { this.setEntityClass(Wishlist.class); }
}
