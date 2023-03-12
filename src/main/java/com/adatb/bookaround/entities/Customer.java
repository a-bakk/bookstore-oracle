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
@Table(name = "customer")
public class Customer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long customerId;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private Date createdAt;
    @Column
    private Date lastLogin;
    @Column
    private boolean admin;
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
    @Column
    private Date regularSince;
}
