package com.adatb.bookaround.entities.compositepk;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Embeddable
public class GenreId implements Serializable {
    @Column
    private Long bookId;
    @Column
    private String genreName;

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof GenreId other)) {
            return false;
        }
        return Objects.equals(bookId, other.bookId) &&
                Objects.equals(genreName, other.genreName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId, genreName);
    }
}
