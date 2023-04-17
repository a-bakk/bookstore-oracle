package com.adatb.bookaround.models;

import com.adatb.bookaround.entities.Author;
import com.adatb.bookaround.entities.Book;
import com.adatb.bookaround.entities.Genre;
import com.adatb.bookaround.models.constants.OnStockStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookWithAuthorsAndGenres implements Serializable {
    private Book book;
    private Set<Author> authors = new HashSet<>();
    private Set<Genre> genres = new HashSet<>();
    private OnStockStatus onStockStatus = OnStockStatus.NONE; // none by default
    public BookWithAuthorsAndGenres(Book book, Set<Author> authors, Set<Genre> genres) {
        this.book = book;
        this.authors = authors;
        this.genres = genres;
    }
}
