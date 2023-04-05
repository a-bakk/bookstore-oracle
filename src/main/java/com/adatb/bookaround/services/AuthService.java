package com.adatb.bookaround.services;

import com.adatb.bookaround.entities.Customer;
import com.adatb.bookaround.models.CustomerCreate;
import com.adatb.bookaround.repositories.CustomerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AuthService {
    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public static boolean isAuthenticated() {
        return !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken);
    }


    public void register(CustomerCreate customerCreate) {
        Customer customer = new Customer();

        customer.setEmail(customerCreate.getEmail());
        customer.setPassword(bCryptPasswordEncoder.encode(customerCreate.getPassword()));
        customer.setFirstName(customerCreate.getFirstName());
        customer.setLastName(customerCreate.getLastName());
        customer.setCreatedAt(LocalDate.now());
        customer.setLastLogin(null);
        customer.setAdmin(false);
        customer.setStreet(customerCreate.getStreet());
        customer.setCity(customerCreate.getCity());
        customer.setStateOrRegion(customerCreate.getStateOrRegion());
        customer.setPostcode(customerCreate.getPostcode());
        customer.setCountry(customerCreate.getCountry());
        customer.setRegularSince(null);

        customerDao.create(customer);
    }
}
