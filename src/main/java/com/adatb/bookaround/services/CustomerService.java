package com.adatb.bookaround.services;

import com.adatb.bookaround.entities.*;
import com.adatb.bookaround.entities.compositepk.ContainsId;
import com.adatb.bookaround.models.CustomerDetails;
import com.adatb.bookaround.models.ShoppingCart;
import com.adatb.bookaround.models.ShoppingCartItem;
import com.adatb.bookaround.repositories.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService implements UserDetailsService {

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ContainsDao containsDao;

    @Autowired
    private InvoiceDao invoiceDao;

    @Autowired
    private NotificationDao notificationDao;

    private static final boolean SHIPPED_ORDER = false;
    private static final boolean PICKUP_ORDER = true;

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

    public boolean createOrderWithShipping(ShoppingCart shoppingCart, CustomerDetails customerDetails) {
        Customer customer = customerDao.find(customerDetails.getCustomerId());
        Long orderSum = shoppingCart.calculateSum();

        // creation of the order itself
        Order order = new Order();
        order.setCreatedAt(LocalDate.now());
        order.setShipped(false);
        order.setPickup(SHIPPED_ORDER);
        order.setCustomer(customer);

        order = orderDao.create(order);

        // adding books to the order
        for (ShoppingCartItem item : shoppingCart.getItems()) {
            ContainsId containsId = new ContainsId(order, item.getBookModel().getBook());
            containsDao.create(new Contains(containsId, item.getCount()));
        }

        // creating invoice for order
        Invoice invoice = new Invoice();
        invoice.setValue(orderSum);
        invoice.setPaid(false);
        invoice.setOrder(order);

        invoiceDao.create(invoice);
        return true;
    }

}
