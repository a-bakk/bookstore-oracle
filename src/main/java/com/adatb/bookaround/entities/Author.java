package com.adatb.bookaround.entities;

import com.adatb.bookaround.entities.compositepk.AuthorId;
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
@Table(name = "author")
public class Author implements Serializable, Comparable<Author> {
    @EmbeddedId
    private AuthorId authorId;

    // required for book modification method
    @Override
    public int compareTo(Author other) {
        return this.authorId.getFirstName().compareTo(other.authorId.getFirstName()) +
            this.authorId.getLastName().compareTo(other.authorId.getLastName());
    }
}
