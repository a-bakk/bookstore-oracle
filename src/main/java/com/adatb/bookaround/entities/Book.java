package com.adatb.bookaround.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "book")
public class Book implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "book_seq")
    @SequenceGenerator(name = "book_seq", sequenceName = "book_seq", allocationSize = 1)
    @Column(name = "book_id")
    private Long bookId;
    @Column(nullable = false)
    private String title;
    @Column
    private String description;
    @Column
    private String cover;
    @Column
    private Double weight;
    @Column
    private Long price;
    @Column
    private Integer numberOfPages;
    @Column
    private LocalDate publishedAt;
    @Column
    private String publisher;
    @Column(nullable = false, unique = true)
    private String Isbn;
    @Column
    private String language;
    @Column
    private Long discountedPrice;
}
