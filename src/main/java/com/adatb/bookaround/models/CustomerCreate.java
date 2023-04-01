package com.adatb.bookaround.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CustomerCreate {
    private Long customerId;
    private String email;
    private String password;
    private String repassword;
    private String firstName;
    private String lastName;
    private String street;
    private String city;
    private String stateOrRegion;
    private String postcode;
    private String country;
}
