package com.adatb.bookaround.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ShoppingCartItem {
    private BookWithAuthorsAndGenres bookModel;
    private int count;
}
