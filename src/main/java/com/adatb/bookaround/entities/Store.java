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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
