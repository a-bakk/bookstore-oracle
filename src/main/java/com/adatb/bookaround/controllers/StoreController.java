package com.adatb.bookaround.controllers;

import com.adatb.bookaround.entities.*;
import com.adatb.bookaround.entities.compositepk.StockId;
import com.adatb.bookaround.models.BookWithAuthorsAndGenres;
import com.adatb.bookaround.models.CustomerDetails;
import com.adatb.bookaround.models.StoreWithBusinessHours;
import com.adatb.bookaround.models.constants.OnStockStatus;
import com.adatb.bookaround.services.AuthService;
import com.adatb.bookaround.services.BookService;
import com.adatb.bookaround.services.CustomerService;
import com.adatb.bookaround.services.StoreService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class StoreController {

    private static final Logger logger = LogManager.getLogger(StoreService.class);

    @Autowired
    private BookService bookService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private StoreService storeService;

        @GetMapping("/index")
        public String showIndex(Model model, @AuthenticationPrincipal CustomerDetails customerDetails) {
            model.addAttribute("stockList", storeService.getStockForEachStore());
            model.addAttribute("bookList", bookService.getAllBookModels());
            model.addAttribute("activePage", "index");
            model.addAttribute("currentCustomer", customerDetails);
            return "index";
        }

        @GetMapping("/latest-additions")
        public String showLatestAdditions(Model model, @AuthenticationPrincipal CustomerDetails customerDetails) {
            model.addAttribute("bookList", bookService.getLatestBooks());
            model.addAttribute("activePage", "latest-additions");
            model.addAttribute("currentCustomer", customerDetails);
            return "latest-additions";
        }

        @GetMapping("/bestsellers")
        public String showPopularBooks(Model model, @AuthenticationPrincipal CustomerDetails customerDetails) {
            model.addAttribute("bookList", bookService.getPopularBooks());
            model.addAttribute("activePage", "bestsellers");
            model.addAttribute("genresWithNumberOfBooks", bookService.getGenreListAndNumberOfBooksPerGenre());
            model.addAttribute("currentCustomer", customerDetails);
            return "bestsellers";
        }

        @GetMapping("/notifications")
        public String showNotificationsForCustomer(Model model,
                @AuthenticationPrincipal CustomerDetails customerDetails,
                RedirectAttributes redirectAttributes) {
            if (!AuthService.isAuthenticated()) {
                redirectAttributes.addFlashAttribute("requiresAuthentication",
                        "A funkció eléréséhez előbb jelentkezzen be!");
                return "redirect:/auth";
            }
            model.addAttribute("notificationList",
                    customerService.getNotificationsByCustomerId(customerDetails.getCustomerId()));
        model.addAttribute("activePage", "notifications");
        model.addAttribute("currentCustomer", customerDetails);
        return "notifications";
    }

    @PostMapping("/delete-notification")
    public String deleteNotification(Long notificationId, RedirectAttributes redirectAttributes,
                                     @AuthenticationPrincipal CustomerDetails customerDetails) {
        if (!AuthService.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("requiresAuthentication",
                    "A funkció eléréséhez előbb jelentkezzen be!");
            return "redirect:/auth";
        }

        storeService.deleteNotificationById(notificationId);
        redirectAttributes.addFlashAttribute("notificationDeletionVerdict", "Értesítés törtlése sikeres!");

        return "redirect:/notifications";
    }

    @GetMapping("/admin-panel")
    public String showAdminPanel(Model model, @AuthenticationPrincipal CustomerDetails customerDetails,
                                 RedirectAttributes redirectAttributes) {
        if (!AuthService.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("requiresAuthentication",
                    "A funkció eléréséhez előbb jelentkezzen be!");
            return "redirect:/auth";
        }
        if (!customerDetails.isAdmin()) {
            return "redirect:/index";
        }
        model.addAttribute("customerList", customerService.getCustomersWithOrderCount());
        model.addAttribute("customersWithLongestWishlists",
                customerService.getCustomersWithLargestWishlists());
        model.addAttribute("customerWithMostRecentOrder", customerService.getCustomerWithMostRecentOrder());
        model.addAttribute("mostPopularAuthor", customerService.getMostPopularAuthorByOrders());
        model.addAttribute("mostPopularGenre", customerService.getMostPopularGenreByOrders());
        model.addAttribute("averagePricePerGenre", customerService.getAveragePricePerGenre());
        model.addAttribute("mostExpensiveAuthors", customerService.getMostExpensiveAuthors());
        model.addAttribute("inventoryForEachStore", customerService.getNumberOfBooksForEachStore());
        model.addAttribute("numberOfUnsoldBooks", storeService.getNumberOfUnsoldBooks());
        model.addAttribute("revenueList", customerService.getRevenueForEachMonth());
        model.addAttribute("activePage", "admin-panel");
        model.addAttribute("currentCustomer", customerDetails);
        return "admin-panel";
    }

    @GetMapping("/admin-panel-create")
    public String showCreateAdminPanel(Model model, @AuthenticationPrincipal CustomerDetails customerDetails,
                                       RedirectAttributes redirectAttributes) {
        if (!AuthService.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("requiresAuthentication",
                    "A funkció eléréséhez előbb jelentkezzen be!");
            return "redirect:/auth";
        }
        if (!customerDetails.isAdmin()) {
            return "redirect:/index";
        }
        model.addAttribute("newBook", new Book());
        model.addAttribute("activePage", "admin-panel");
        model.addAttribute("currentCustomer", customerDetails);
        return "admin-panel-create";
    }

    @GetMapping("/book/{bookId}")
    public String showBookById(Model model,
                               @PathVariable Long bookId,
                               @AuthenticationPrincipal CustomerDetails customerDetails) {
        BookWithAuthorsAndGenres curr = bookService.getBookWithAuthorsAndGenresById(bookId);
        Set<Author> authors = curr.getAuthors();
        Set<Genre> genres = curr.getGenres();
        model.addAttribute("bookModel", curr);
        model.addAttribute("authorsAsString",
                BookService.joinStrings(authors.stream().map(author -> author.getAuthorId().getFirstName()
                        + " " + author.getAuthorId().getLastName()).collect(Collectors.toSet())));
        model.addAttribute("genresAsString",
                BookService.joinStrings(genres.stream().map(genre -> genre.getGenreId().getGenreName())
                        .collect(Collectors.toSet())));
        model.addAttribute("recommendationModels", bookService.getRecommendationsByBookId(bookId));
        model.addAttribute("isLoggedIn", AuthService.isAuthenticated());
        if (AuthService.isAuthenticated()) {
            model.addAttribute("wishlistModels",
                    customerService.getWishlistsForCustomer(customerDetails.getCustomerId()));
            model.addAttribute("numberOfWishlists",
                    customerService.getNumberOfWishlistsForCustomer(customerDetails.getCustomerId()));
        }
        model.addAttribute("currentCustomer", customerDetails);
        return "book";
    }

    @PostMapping("/filter-books")
    public String filterBooks(@RequestParam(name = "filterTitle") String title,
                              @RequestParam(name = "filterAuthor") String author,
                              @RequestParam(name = "filterGenre") String genre,
                              @RequestParam(name = "filterPrice") String limit,
                              RedirectAttributes redirectAttributes) {
        var books = bookService.filterBooks(title, author, genre, limit);
        redirectAttributes.addFlashAttribute("filteredBooks", books);
        redirectAttributes.addFlashAttribute("filteredBooksSize", books.size());
        return "redirect:/index";
    }

    @PostMapping("/filter-bestsellers-by-genre")
    public String filterBestsellersByGenre(@RequestParam(name = "chosenGenreName") String genreName,
                                           RedirectAttributes redirectAttributes) {
        var books = bookService.getBestsellersByGenre(genreName);
        redirectAttributes.addFlashAttribute("filteredBookList",
                books == null || genreName == null
                        ? null
                        : books);
        return "redirect:/bestsellers";
    }

    @PostMapping("/add-book")
    public String addBookWithAuthorsAndGenres(Book newBook,
                                              @RequestParam(name = "authors") String authors,
                                              @RequestParam(name = "genres") String genres,
                                              RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("entityCreationVerdict",
                bookService.createBookWithAuthorsAndGenres(newBook, authors, genres)
                        ? "Új könyv sikeresen létrehozva!"
                        : "Új könyv létrehozása sikertelen!"
        );
        return "redirect:/admin-panel-create";
    }

    @PostMapping("/delete-book")
    public String deleteBookById(@RequestParam(name = "delete-book-id") Long deleteBookId,
                                 RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("entityDeletionVerdict",
                bookService.deleteBookById(deleteBookId)
                        ? "Könyv sikeresen törölve!"
                        : "Könyv törlése sikertelen! (id = " + deleteBookId + ")"
        );
        return "redirect:/index";
    }

    @PostMapping("/modify-book")
    public String modifyBookById(@RequestParam Long modifyBookId,
                                 @RequestParam String modifyTitle,
                                 @RequestParam String modifyDescription,
                                 @RequestParam String modifyCover,
                                 @RequestParam Double modifyWeight,
                                 @RequestParam Long modifyPrice,
                                 @RequestParam Integer modifyNumberOfPages,
                                 @RequestParam LocalDate modifyPublishedAt,
                                 @RequestParam String modifyPublisher,
                                 @RequestParam String modifyIsbn,
                                 @RequestParam String modifyLanguage,
                                 // works with string only for some reason
                                 @RequestParam(required = false) String modifyDiscountedPrice,
                                 @RequestParam String modifyAuthors,
                                 @RequestParam String modifyGenres,
                                 RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("bookModificationVerdict",
                bookService.modifyBookById(
                        modifyBookId, modifyTitle, modifyDescription,
                        modifyCover, modifyWeight, modifyPrice,
                        modifyNumberOfPages, modifyPublishedAt,
                        modifyPublisher, modifyIsbn, modifyLanguage,
                        modifyDiscountedPrice.isEmpty() ? null : Long.parseLong(modifyDiscountedPrice),
                        modifyAuthors, modifyGenres
                )
                        ? "Könyv sikeresen módosítva!"
                        : "Könyv módosítása sikertelen! (id = " + modifyBookId + ")");
        return "redirect:/book/" + modifyBookId;
    }

    @GetMapping("/stores")
    public String showStores(Model model, @AuthenticationPrincipal CustomerDetails customerDetails) {
        model.addAttribute("activePage", "stores");
        model.addAttribute("currentCustomer", customerDetails);
        model.addAttribute("storeList", storeService.getAllStores());
        if (customerDetails != null && customerDetails.isAdmin()) {
            model.addAttribute("newStore", new Store());
        }
        return "stores";
    }

    @GetMapping("/store/{storeId}")
    public String showStoreById(Model model, @PathVariable Long storeId, @AuthenticationPrincipal CustomerDetails customerDetails) {
        StoreWithBusinessHours storeWithBusinessHours = storeService.getStoreById(storeId);
        Store store = storeWithBusinessHours.getStore();
        if (storeWithBusinessHours.getBusinessHours() == null) {
            logger.warn("BusinessHours not found");
            return "redirect:/stores";
        }
        ArrayList<BusinessHours> businessHours = storeWithBusinessHours.getBusinessHours();
        ArrayList<BusinessHours> businessHoursFilled = new ArrayList<>();
        short in = 0;
        for (short i = 0; i < 7; i++) {
            if (storeWithBusinessHours.listContainsDayOfWeek((short)(i+1))) {
                businessHoursFilled.add(i, businessHours.get(in));

                businessHoursFilled.get(i).setOpeningTime(StoreService.convertStringTimeFrom12HTo24HFormat(businessHours.get(in).getOpeningTime()));
                businessHoursFilled.get(i).setClosingTime(StoreService.convertStringTimeFrom12HTo24HFormat(businessHours.get(in).getClosingTime()));

                in++;
            } else {
                businessHoursFilled.add(new BusinessHours(null, (short)(i+1), null, null, null));
            }
        }
        List<Stock> stock = storeService.getStockForStoreById(storeId);
        Map<BookWithAuthorsAndGenres, Integer> books = new HashMap<>();
        for (Stock stock1 : stock) {
            books.put(bookService.getEncapsulatedBook(stock1.getStockId().getBook().getBookId()), stock1.getCount());
        }

        model.addAttribute("activePage", "stores");
        model.addAttribute("currentCustomer", customerDetails);
        model.addAttribute("store", store);
        model.addAttribute("businessHoursList", businessHours);
        model.addAttribute("businessHoursFilledList", businessHoursFilled);
        model.addAttribute("stockMap", books);
        return "store";
    }

    @PostMapping("/modify-store")
    public String modifyStoreById(@RequestParam Long modifyStoreId,
                                 @RequestParam String modifyName,
                                 @RequestParam String modifyCountry,
                                 @RequestParam String modifyStateOrRegion,
                                 @RequestParam String modifyPostcode,
                                 @RequestParam String modifyCity,
                                 @RequestParam String modifyStreet,
                                 @RequestParam(required = false) String[] modifyOpeningTimes,
                                 @RequestParam(required = false) String[] modifyClosingTimes,
                                 RedirectAttributes redirectAttributes) {


        storeService.modifyStoreById(modifyStoreId, modifyName, modifyCountry,
                modifyStateOrRegion, modifyPostcode, modifyCity, modifyStreet,
                modifyOpeningTimes, modifyClosingTimes);
        return "redirect:/store/" + modifyStoreId;
    }

    @PostMapping("/delete-store")
    public String deleteStoreById(@RequestParam Long deleteStoreId,
                                  RedirectAttributes redirectAttributes) {

        storeService.deleteStoreById(deleteStoreId);
        return "redirect:/stores";
    }

    @PostMapping("/add-store")
    public String addStore(@RequestParam String createName,
                              @RequestParam String createCountry,
                              @RequestParam String createStateOrRegion,
                              @RequestParam String createPostcode,
                              @RequestParam String createCity,
                              @RequestParam String createStreet,
                              RedirectAttributes redirectAttributes) {

        storeService.createStore(createName, createCountry, createStateOrRegion, createPostcode, createCity, createStreet);
        return "redirect:/stores";
    }

    @PostMapping("/add-stock")
    public String addStock(@RequestParam Long addStoreId,
                           @RequestParam String addBookTitle,
                           @RequestParam Integer addBookCount,
                           RedirectAttributes redirectAttributes) {

        storeService.addStock(addStoreId, addBookTitle, addBookCount);
        return "redirect:/store/" + addStoreId;
    }

    @PostMapping("/delete-stock")
    public String deleteStock(@RequestParam Long storeId,
                              @RequestParam Long bookId,
                              RedirectAttributes redirectAttributes) {

        storeService.deleteStock(storeId, bookId);
        return "redirect:/store/" + storeId;
    }

}
