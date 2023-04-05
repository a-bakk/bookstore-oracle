package com.adatb.bookaround.entities;

import com.adatb.bookaround.entities.compositepk.GenreId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
