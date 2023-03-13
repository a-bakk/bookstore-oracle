package com.adatb.bookaround.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "book")
public class Book implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long bookId;
    @Column(nullable = false)
    private String name;
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
    private Date publishedAt;
    @Column
    private String publisher;
    @Column(nullable = false, unique = true)
    private String Isbn;
    @Column
    private String language;
    @Column
    private Long discountedPrice;
}
