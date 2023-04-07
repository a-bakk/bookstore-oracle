package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Customer;
import com.adatb.bookaround.models.BookWithAuthorsAndGenres;
import jakarta.persistence.NoResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.regex.Pattern;


@Repository
public class CustomerDao extends AbstractJpaDao<Customer> {
    public CustomerDao() { this.setEntityClass(Customer.class); }

    private static final Logger logger = LogManager.getLogger(CustomerDao.class);


    /**
     * Keresés email alapján felhasználóra
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

    /**
     * Keresés felhasználóra id alapján
     * @param id felhasználó idje
     * @return az idhoz tartozó profil
     */
    public Customer findById(int id) {
        Customer result;
        try {
            result = entityManager.createQuery("SELECT c FROM Customer c WHERE c.customerId = :id", Customer.class)
                    .setParameter(id, id).getSingleResult();
        } catch (NoResultException e) {
            logger.warn("User could not be found with the following id: " + id);
            return null;
        }
        return result;
    }

    /**
     * Egyezik-e a két jelszó
     * @param customer a felhasználó
     * @param password ellenőrizendő jelszó
     * @return ha jó a jelszó true, különben false
     */
    /*TODO: Change password to hashed password??*/
    public boolean isPasswordCorrect(Customer customer, String password) {
        if (password == null || password.isEmpty()) return false;
        if (customer == null) return false;
        return customer.getPassword().equals(password);
    }

    /**
     * Összes felhasználó listázása
     * @return Az összes felhasználóból alkotott lista.
     */
    public List<Customer[]> findAllCustomers() {

        String jpql = "SELECT Customer FROM Customer";

        List<Customer[]> resultList = entityManager.createQuery(jpql, Customer[].class)
                .getResultList();

        return resultList;
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{6,}$";
        Pattern pat = Pattern.compile(passwordRegex);
        if (password == null)
            return false;
        return pat.matcher(password).matches();
    }
}











