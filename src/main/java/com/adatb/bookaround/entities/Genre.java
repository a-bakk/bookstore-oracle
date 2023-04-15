package com.adatb.bookaround.entities;

import com.adatb.bookaround.entities.compositepk.GenreId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "genre")
public class Genre implements Serializable, Comparable<Genre> {
    @EmbeddedId
    private GenreId genreId;

    // required by the book modification method which uses a ConcurrentSkipListSet
    @Override
    public int compareTo(Genre other) {
        return this.genreId.getGenreName().compareTo(other.getGenreId().getGenreName());
    }
}
