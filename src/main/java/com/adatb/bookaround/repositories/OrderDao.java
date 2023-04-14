package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Order;
import com.adatb.bookaround.models.OrderWithContentAndInvoice;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class OrderDao extends AbstractJpaDao<Order> {
    public OrderDao () { this.setEntityClass(Order.class); }

    @Autowired
    private InvoiceDao invoiceDao;

    @Autowired
    private ContainsDao containsDao;

    @Autowired
    private BookDao bookDao;

    public List<OrderWithContentAndInvoice> findOrdersForCustomer(Long customerId) {
        List<Order> orders = entityManager.createQuery("SELECT o " +
                        "FROM Order o " +
                        "WHERE o.customer.customerId = :customerId", Order.class)
                .setParameter("customerId", customerId)
                .getResultList();

        return orders.stream().map(order -> {
            OrderWithContentAndInvoice entity = new OrderWithContentAndInvoice();
            entity.setOrder(order);
            entity.setInvoice(invoiceDao.findInvoiceByOrder(order.getOrderId()));
            containsDao.findByOrder(order.getOrderId()).forEach(contains -> {
                entity.getBooks().add(
                        bookDao.encapsulateBook(
                                bookDao.find(contains.getContainsId().getBook().getBookId())
                        )
                );
            });
            return entity;
        }).toList();
    }
}
