package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Customer;
import com.adatb.bookaround.models.CustomerCreate;
import com.adatb.bookaround.models.CustomerDetails;
import com.adatb.bookaround.models.CustomerWithOrderCount;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;


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

    /**
     * [Összetett lekérdezés]
     * Az ügyfelek és a rendeléseiknek számának lekérdezése.
     *
     * @return modellek listája, melyben az ügyfél és a rendeléseinek száma van tárolva
     */
    public List<CustomerWithOrderCount> findNumberOfOrdersForAllCustomers() {
        String jpql = "SELECT c, COUNT(*) as number_of_orders " +
                "FROM Order o " +
                "JOIN Customer c ON o.customer.customerId = c.customerId " +
                "GROUP BY c " +
                "ORDER BY number_of_orders DESC";

        List<Object[]> resultList = entityManager.createQuery(jpql, Object[].class).getResultList();

        List<CustomerWithOrderCount> hasOrders = resultList.stream().map(row -> {
            Customer customer = find(((Customer) row[0]).getCustomerId());
            return new CustomerWithOrderCount(customer, (Long) row[1]);
        }).toList();

        List<CustomerWithOrderCount> hasNoOrders = findAll().stream().filter(customer -> {
            for (CustomerWithOrderCount c : hasOrders) {
                if (Objects.equals(c.getCustomer().getCustomerId(), customer.getCustomerId()))
                    return false;
            }
            return true;
        }).map(customer -> new CustomerWithOrderCount(customer, 0L)).toList();

        return Stream.concat(hasOrders.stream(), hasNoOrders.stream()).toList();
    }

    /**
     * [Összetett lekérdezés]
     * Meghatározza, hogy melyik ügyfél rendelt legutóbb.
     *
     * @return a legfrisebb rendeléssel rendelkező ügyfél
     */
    public Customer findCustomerWithMostRecentOrder() {
        String jpql = "SELECT c " +
                "FROM Order o " +
                "JOIN Customer c ON o.customer.customerId = c.customerId " +
                "WHERE o.createdAt = (" +
                "SELECT MAX(newest.createdAt) FROM Order newest" +
                ") " +
                "ORDER BY c.customerId DESC";

        TypedQuery<Customer> query = entityManager.createQuery(jpql, Customer.class);
        var results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Transactional
    public boolean modifyCustomerById(Long customerId, CustomerCreate modifiedCustomer, String newPassword) {
        Query query = entityManager.createQuery("UPDATE Customer c " +
                        "SET c.firstName = :modifiedFirstName, " +
                        "c.lastName = :modifiedLastName, " +
                        "c.country = :modifiedCountry, " +
                        "c.stateOrRegion = :modifiedStateOrRegion, " +
                        "c.postcode = :modifiedPostcode, " +
                        "c.city = :modifiedCity, " +
                        "c.street = :modifiedStreet, " +
                        "c.password = :modifiedPassword " +
                        "WHERE c.id = :customerId")
                .setParameter("modifiedFirstName", modifiedCustomer.getFirstName())
                .setParameter("modifiedLastName", modifiedCustomer.getLastName())
                .setParameter("modifiedCountry", modifiedCustomer.getCountry())
                .setParameter("modifiedStateOrRegion", modifiedCustomer.getStateOrRegion())
                .setParameter("modifiedPostcode", modifiedCustomer.getPostcode())
                .setParameter("modifiedCity", modifiedCustomer.getCity())
                .setParameter("modifiedStreet", modifiedCustomer.getStreet())
                .setParameter("modifiedPassword", newPassword)
                .setParameter("customerId", customerId);

        return query.executeUpdate() == 1;
    }
}











