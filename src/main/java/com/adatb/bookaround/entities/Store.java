package com.adatb.bookaround.entities;

import jakarta.persistence.*;
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
@Table(name = "store")
public class Store implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "store_seq")
    @SequenceGenerator(name = "store_seq", sequenceName = "store_seq", allocationSize = 1)
    @Column(name = "store_id")
    private Long storeId;
    @Column(nullable = false)
    private String name;
    @Column
    private String street;
    @Column
    private String city;
    @Column
    private String stateOrRegion;
    @Column
    private String postcode;
    @Column
    private String country;
}
