package com.adatb.bookaround.models;

import com.adatb.bookaround.entities.Customer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomerDetails implements UserDetails {

    private final String ROLE_PREFIX = "ROLE_";
    private Long customerId;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private LocalDate createdAt;
    private LocalDateTime lastLogin;
    private boolean admin;
    private String street;
    private String city;
    private String stateOrRegion;
    private String postcode;
    private String country;
    private LocalDateTime regularSince;

    public CustomerDetails(Customer customer) {
        this.customerId = customer.getCustomerId();
        this.email = customer.getEmail();
        this.password = customer.getPassword();
        this.firstName = customer.getFirstName();
        this.lastName = customer.getLastName();
        this.createdAt = customer.getCreatedAt();
        this.lastLogin = customer.getLastLogin();
        this.admin = customer.isAdmin();
        this.street = customer.getStreet();
        this.city = customer.getCity();
        this.stateOrRegion = customer.getStateOrRegion();
        this.postcode = customer.getPostcode();
        this.country = customer.getCountry();
        this.regularSince = customer.getRegularSince();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + "USER"));
        if (this.admin) {
            authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + "ADMIN"));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
