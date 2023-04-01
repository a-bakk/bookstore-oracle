package com.adatb.bookaround.controllers;

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

        logger.warn("test");

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
    public String showNotificationsForCustomer(Model model, @PathVariable Long cid) {
        model.addAttribute("notificationList", customerService.getNotificationsByCustomerId(cid));
        model.addAttribute("activePage", "notifications");
        return "notifications";
    }

    @GetMapping("/admin-panel")
    public String showAdminPanel(Model model) {
        model.addAttribute("customerList", customerService.getCustomers());
        model.addAttribute("storeList", storeService.getAllStores());
        model.addAttribute("orderList", storeService.getAllOrders());
        model.addAttribute("activePage", "admin-panel");
        return "admin-panel";
    }

}
