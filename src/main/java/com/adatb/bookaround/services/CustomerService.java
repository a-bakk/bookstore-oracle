package com.adatb.bookaround.services;

import com.adatb.bookaround.entities.Customer;
import com.adatb.bookaround.entities.Notification;
import com.adatb.bookaround.models.CustomerDetails;
import com.adatb.bookaround.repositories.CustomerDao;
import com.adatb.bookaround.repositories.NotificationDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService implements UserDetailsService {

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private NotificationDao notificationDao;

    private static final Logger logger = LogManager.getLogger(CustomerService.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerDao.findByEmail(username);
        if (customer == null) {
            logger.warn("Empty user is being loaded with the following email: " + username);
            return null;
        }
        return new CustomerDetails(customer);
    }

    public List<Notification> getNotificationsByCustomerId(Long customerId) {
        List<Notification> notifs = notificationDao.findByCustomerId(customerId);
        if (notifs == null || notifs.isEmpty()) {
            logger.warn("No notifications could be loaded for user with id: " + customerId);
            return new ArrayList<>();
        }
        return notifs;
    }

    public List<Customer> getCustomers() {
        List<Customer> customers = customerDao.findAll();
        if (customers == null || customers.isEmpty()) {
            logger.warn("Customers could not be loaded!");
            return new ArrayList<>();
        }
        return customers;
    }

}
