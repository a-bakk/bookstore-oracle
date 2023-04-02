package com.adatb.bookaround.controllers;

import com.adatb.bookaround.entities.Book;
import com.adatb.bookaround.services.BookService;
import com.adatb.bookaround.services.CustomerService;
import com.adatb.bookaround.services.StoreService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class StoreController {

    @Autowired
    private BookService bookService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private StoreService storeService;
    private static final Logger logger = LogManager.getLogger(StoreController.class);

    @GetMapping("/index")
    public String showIndex(Model model) {

        model.addAttribute("stockList", storeService.getStockForEachStore());

        return "index";
    }

    @GetMapping("/latest-additions")
    public String showLatestAdditions(Model model) {
        model.addAttribute("bookList", bookService.getLatestBooks());
        model.addAttribute("activePage", "latest-additions");
        return "latest-additions";
    }

    @GetMapping("/bestsellers")
    public String showPopularBooks(Model model) {
        model.addAttribute("bookList", bookService.getPopularBooks());
        model.addAttribute("activePage", "bestsellers");
        return "bestsellers";
    }

    @GetMapping("/notifications/{cid}")
    public String showNotificationsForCustomer(Model model,
                                               @PathVariable Long cid) {
        model.addAttribute("notificationList", customerService.getNotificationsByCustomerId(cid));
        model.addAttribute("activePage", "notifications");
        return "notifications";
    }

    @GetMapping("/admin-panel")
    public String showAdminPanel(Model model) {
        model.addAttribute("bookModelList", bookService.getAllBookModels());
        model.addAttribute("customerList", customerService.getCustomers());
        model.addAttribute("storeList", storeService.getAllStores());
        model.addAttribute("orderList", storeService.getAllOrders());
        model.addAttribute("activePage", "admin-panel");
        return "admin-panel";
    }

    @GetMapping("/admin-panel-create")
    public String showCreateAdminPanel(Model model) {
        model.addAttribute("newBook", new Book());
        model.addAttribute("activePage", "admin-panel");
        return "admin-panel-create";
    }

    @GetMapping("/book/{bookId}")
    public String showBookById(Model model,
                               @PathVariable Long bookId) {
        model.addAttribute("bookModel", bookService.getBookWithAuthorsAndGenresById(bookId));
        model.addAttribute("recommendationModels", bookService.getRecommendationsByBookId(bookId));
        return "book";
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

}
