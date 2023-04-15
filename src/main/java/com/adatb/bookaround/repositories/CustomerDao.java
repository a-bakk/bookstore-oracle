package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Customer;
import jakarta.persistence.NoResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;


@Repository
public class CustomerDao extends AbstractJpaDao<Customer> {
    private static final Logger logger = LogManager.getLogger(CustomerDao.class);

    public CustomerDao() {
        this.setEntityClass(Customer.class);
    }

    /**
     * Keresés email alapján felhasználóra
     *
     * @param email felhasználó emailje
     * @return az emailhoz tartozó profil
     */
    public Customer findByEmail(String email) {
        if (email == null || email.isEmpty()) return null;
        Customer result;
        try {
            result = entityManager.createQuery("SELECT c FROM Customer c WHERE c.email = :email", Customer.class)
                    .setParameter("email", email).getSingleResult();
        } catch (NoResultException e) {
            logger.warn("User could not be found with the following email: " + email);
            return null;
        }
        return result;
    }
}











