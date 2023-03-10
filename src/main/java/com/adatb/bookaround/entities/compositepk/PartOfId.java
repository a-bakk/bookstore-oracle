package com.adatb.bookaround.entities.compositepk;

import com.adatb.bookaround.entities.Book;
import com.adatb.bookaround.entities.Wishlist;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Embeddable
public class PartOfId implements Serializable {
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "book_id", referencedColumnName = "book_id")
    private Book book;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "wishlist_id", referencedColumnName = "wishlist_id")
    private Wishlist wishlist;

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PartOfId other)) {
            return false;
        }
        return Objects.equals(book, other.book) &&
                Objects.equals(wishlist, other.wishlist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(book, wishlist);
    }
}
