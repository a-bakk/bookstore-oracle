package com.adatb.bookaround.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ShoppingCart {
    private List<ShoppingCartItem> items = new ArrayList<>();

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void addItem(BookWithAuthorsAndGenres book, int count) {
        for (ShoppingCartItem item : items) {
            if (Objects.equals(item.getBookModel().getBook().getBookId(),
                    book.getBook().getBookId())) {
                item.setCount(item.getCount() + count);
                return;
            }
        }
        items.add(new ShoppingCartItem(book, count));
    }

    public void removeItem(BookWithAuthorsAndGenres book) {
        items.removeIf(item -> Objects.equals(item.getBookModel().getBook().getBookId(),
                book.getBook().getBookId()));
    }

    public Long calculateSum() {
        return items.stream().mapToLong(item -> {
            Long price = item.getBookModel().getBook().getDiscountedPrice() == null
                    ? item.getBookModel().getBook().getPrice() : item.getBookModel().getBook().getDiscountedPrice();
            return price * item.getCount();
        }).sum();
    }
}
