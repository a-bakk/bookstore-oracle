package com.adatb.bookaround.services;

import com.adatb.bookaround.entities.*;
import com.adatb.bookaround.entities.compositepk.ContainsId;
import com.adatb.bookaround.entities.compositepk.PartOfId;
import com.adatb.bookaround.models.*;
import com.adatb.bookaround.repositories.*;
import com.aspose.words.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CustomerService implements UserDetailsService {

    private static final Logger logger = LogManager.getLogger(CustomerService.class);
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
    @Autowired
    private StockDao stockDao;
    @Autowired
    private WishlistDao wishlistDao;
    @Autowired
    private BookDao bookDao;
    @Autowired
    private PartOfDao partOfDao;

    @Autowired
    private StoreDao storeDao;

    @Autowired
    private AuthorDao authorDao;

    @Autowired
    private GenreDao genreDao;

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

    @SuppressWarnings("unused")
    public List<Customer> getCustomers() {
        List<Customer> customers = customerDao.findAll();
        if (customers == null || customers.isEmpty()) {
            logger.warn("Customers could not be loaded!");
            return new ArrayList<>();
        }
        return customers;
    }

    public List<CustomerWithOrderCount> getCustomersWithOrderCount() {
        List<CustomerWithOrderCount> customers = customerDao.findNumberOfOrdersForAllCustomers();
        if (customers == null || customers.isEmpty()) {
            logger.warn("Customers with number of orders could not be loaded!");
            return new ArrayList<>();
        }
        return customers;
    }

    public Customer getCustomerWithMostRecentOrder() {
        Customer customer = customerDao.findCustomerWithMostRecentOrder();
        if (customer == null) {
            logger.warn("Customer with most recent order could not be loaded because there are no orders!");
            return null;
        }
        return customer;
    }

    public List<OrderWithContentAndInvoice> getOrdersForCustomer(Long customerId) {
        List<OrderWithContentAndInvoice> orders = orderDao.findOrdersForCustomer(customerId);
        if (orders == null || orders.isEmpty()) {
            logger.warn("No orders could be loaded for customer with id: " + customerId);
            return new ArrayList<>();
        }
        orders.forEach(order -> {
            order.setContentAsString(
                    order.getBooks().stream().map(book -> book.getBook().getTitle())
                            .collect(Collectors.joining(", "))
            );
        });
        return orders;
    }

    public List<WishlistWithContent> getWishlistsForCustomer(Long customerId) {
        List<WishlistWithContent> wishlists = wishlistDao.findWishlistsForCustomer(customerId);
        if (wishlists == null || wishlists.isEmpty()) {
            return new ArrayList<>();
        }
        return wishlists;
    }

    public boolean payInvoice(Long invoiceId) {
        Invoice invoice = invoiceDao.find(invoiceId);
        if (invoice.isPaid())
            return false;
        invoice.setPaid(true);
        invoiceDao.update(invoice);
        return true;
    }

    public boolean deleteOrderById(Long orderId) {
        Order order = orderDao.find(orderId);
        if (order == null)
            return false;
        orderDao.delete(orderId);
        return true;
    }

    public boolean createWishlist(String wishlistName, Long customerId) {
        Wishlist wishlist = new Wishlist();
        Customer customer = customerDao.find(customerId);
        if (customer == null)
            return false;
        wishlist.setCustomer(customer);
        wishlist.setCreatedAt(LocalDateTime.now());
        wishlist.setName(wishlistName);
        wishlistDao.create(wishlist);
        return true;
    }

    public boolean modifyWishlist(String wishListName, Long wishlistId) {
        Wishlist wishlist = wishlistDao.find(wishlistId);
        if (wishlist == null)
            return false;
        wishlist.setName(wishListName);
        wishlistDao.update(wishlist);
        return true;
    }

    public boolean deleteWishlist(Long wishlistId) {
        Wishlist wishlist = wishlistDao.find(wishlistId);
        if (wishlist == null)
            return false;
        wishlistDao.delete(wishlistId);
        return true;
    }

    public Integer getNumberOfWishlistsForCustomer(Long customerId) {
        return wishlistDao.findNumberOfWishlistsForCustomer(customerId);
    }

    public boolean addBookToWishlist(Long wishlistId, Long bookId) {
        Wishlist wishlist = wishlistDao.find(wishlistId);
        Book book = bookDao.find(bookId);
        if (wishlist == null || book == null)
            return false;
        if (wishlistDao.findBooksByWishlistId(wishlistId).stream()
                .anyMatch(entry -> Objects.equals(entry.getBookId(), bookId)))
            return false;
        PartOf partOf = new PartOf();
        PartOfId partOfId = new PartOfId();
        partOfId.setBook(book);
        partOfId.setBook(book);
        partOfId.setWishlist(wishlist);
        partOf.setPartOfId(partOfId);
        partOf.setAddedAt(LocalDateTime.now());
        partOfDao.create(partOf);
        return true;
    }

    public boolean removeBookFromWishlist(Long wishlistId, Long bookId) {
        Wishlist wishlist = wishlistDao.find(wishlistId);
        Book book = bookDao.find(bookId);
        if (wishlist == null || book == null)
            return false;
        PartOf partOf = partOfDao.findByBookAndWishlist(book, wishlist);
        if (partOf == null)
            return false;
        partOfDao.delete(book, wishlist);
        return true;
    }

    public boolean deleteCustomerById(Long customerId) {
        Customer customer = customerDao.find(customerId);
        if (customer == null)
            return false;
        customerDao.delete(customerId);
        return true;
    }

    public boolean createOrder(ShoppingCart shoppingCart, CustomerDetails customerDetails,
                               Boolean orderMode) throws Exception {
        return createOrder(shoppingCart, customerDetails, orderMode, null);
    }

    public boolean createOrder(ShoppingCart shoppingCart, CustomerDetails customerDetails,
                               Boolean orderMode, Long storeId) throws Exception {
        Customer customer = customerDao.find(customerDetails.getCustomerId());
        Long orderSum = shoppingCart.calculateSum();

        // TODO: check if book is on stock

        // creation of the order itself
        Order order = new Order();
        order.setCreatedAt(LocalDate.now());
        order.setShipped(false);
        order.setPickup(orderMode);
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

        if (storeId == null) {
            createInvoicePdf(order, shoppingCart, customerDetails, invoice);
        } else {
            createInvoicePdf(order, shoppingCart, customerDetails, invoice, storeDao.find(storeId));
        }

        // remove books from stock
        for (ShoppingCartItem item : shoppingCart.getItems()) {
            removeBooksFromStock(item.getBookModel().getBook(), item.getCount());
        }

        return true;
    }

    public Author getMostPopularAuthorByOrders() {
        Author author = authorDao.findMostPopularAuthor();
        if (author == null) {
            logger.warn("Most popular author could not be loaded!");
            return null;
        }
        return author;
    }

    public Genre getMostPopularGenreByOrders() {
        Genre genre = genreDao.findMostPopularGenre();
        if (genre == null) {
            logger.warn("Most popular genre could not be loaded!");
            return null;
        }
        return genre;
    }

    public Map<Customer, Long> getCustomersWithLargestWishlists() {
        var customers = wishlistDao.findCustomersWithLargestWishlists();
        if (customers == null) {
            logger.warn("Customers with longest wish lists could not be loaded!");
            return null;
        }
        return customers;
    }

    public Map<String, String> getAveragePricePerGenre() {
        var genres = genreDao.findAveragePricePerGenre();
        if (genres == null) {
            logger.warn("Average prices per genres could not be loaded!");
            return null;
        }
        return genres;
    }

    public Map<String, String> getMostExpensiveAuthors() {
        var authors = authorDao.findMostExpensiveAuthors();
        if (authors == null) {
            logger.warn("The most expensive authors could not be loaded!");
            return null;
        }
        return authors;
    }

    private void createInvoicePdf(Order order, ShoppingCart shoppingCart, CustomerDetails customerDetails,
                                  Invoice invoice) throws Exception {
        createInvoicePdf(order, shoppingCart, customerDetails, invoice, null);
    }

    private void createInvoicePdf(Order order, ShoppingCart shoppingCart, CustomerDetails customerDetails,
                                  Invoice invoice, Store store) throws Exception {
        File file = ResourceUtils.getFile("classpath:static/invoice-template.docx");
        Document document = new Document(file.getAbsolutePath());

        document.getRange().replace("{date}", order.getCreatedAt().toString(), new FindReplaceOptions());
        document.getRange().replace("{invoiceId}", invoice.getInvoiceId().toString(), new FindReplaceOptions());

        document.getRange().replace("{customerName}", customerDetails.getFirstName() + " "
                + customerDetails.getLastName(), new FindReplaceOptions());
        document.getRange().replace("{customerEmail}", customerDetails.getEmail(), new FindReplaceOptions());
        String address = customerDetails.getStreet() + ", " + customerDetails.getCity()
                + ", " + customerDetails.getStateOrRegion() + ", " + customerDetails.getCountry()
                + ", " + customerDetails.getPostcode();
        document.getRange().replace("{customerAddress}", address, new FindReplaceOptions());
        document.getRange().replace("{orderType}",
                store == null ? "A rendelés típusa: kiszállítás"
                        : "A rendelés átvehető innen: " + store.getName(), new FindReplaceOptions());

        // books in the third table
        Table table = (Table) document.getChild(NodeType.TABLE, 2, true);
        Row template = table.getRows().get(1);

        table.getRows().remove(template);

        for (ShoppingCartItem item : shoppingCart.getItems()) {
            Row newRow = (Row) template.deepClone(true);

            newRow.getCells().get(0) // first row
                    .getRange().replace("{bookTitle}",
                            item.getBookModel().getBook().getTitle(), new FindReplaceOptions());
            newRow.getCells().get(1)
                    .getRange().replace("{quantity}",
                            String.valueOf(item.getCount()), new FindReplaceOptions());
            newRow.getCells().get(2)
                    .getRange().replace("{pricePerUnit}",
                            String.valueOf(item.getBookModel().getBook().getDiscountedPrice() == null
                                    ? item.getBookModel().getBook().getPrice()
                                    : item.getBookModel().getBook().getDiscountedPrice()), new FindReplaceOptions());
            newRow.getCells().get(3)
                    .getRange().replace("{totalPrice}",
                            String.valueOf(item.getCount()
                                    * (item.getBookModel().getBook().getDiscountedPrice() == null
                                    ? item.getBookModel().getBook().getPrice()
                                    : item.getBookModel().getBook().getDiscountedPrice())), new FindReplaceOptions());

            table.getRows().add(newRow);
        }

        document.getRange().replace("{total}", String.valueOf(shoppingCart.calculateSum()),
                new FindReplaceOptions());

        document.save("src/main/resources/static/invoices/invoice_"
                + invoice.getInvoiceId() + ".pdf", SaveFormat.PDF);
    }

    private void removeBooksFromStock(Book book, Integer count) {
        List<Stock> stocks = stockDao.findStocksByBookId(book.getBookId());
        for (Stock stock : stocks) {
            int remove = Math.min(stock.getCount(), count);
            stock.setCount(stock.getCount() - remove);
            if (stock.getCount() == 0)
                stockDao.deleteByStockId(stock.getStockId());
            else stockDao.update(stock);
            count -= remove;
            if (count == 0)
                return;
        }
    }

}
