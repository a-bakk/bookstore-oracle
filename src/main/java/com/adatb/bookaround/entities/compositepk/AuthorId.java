package com.adatb.bookaround.entities.compositepk;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Embeddable
public class AuthorId implements Serializable {
    @Column
    private Long bookId;
    @Column
    private String firstName;
    @Column
    private String lastName;

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AuthorId other)) {
            return false;
        }
        return Objects.equals(bookId, other.bookId) &&
                Objects.equals(firstName, other.firstName) &&
                Objects.equals(lastName, other.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId, firstName, lastName);
    }
}
