package com.adatb.bookaround.models;

import com.adatb.bookaround.entities.Book;
import com.adatb.bookaround.entities.Wishlist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WishlistWithContent {
    private Wishlist wishlist;
    private List<Book> books = new ArrayList<>();
}
