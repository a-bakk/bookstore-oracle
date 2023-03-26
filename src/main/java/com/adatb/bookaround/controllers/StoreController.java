package com.adatb.bookaround.controllers;

import com.adatb.bookaround.services.BookService;
import com.adatb.bookaround.services.CustomerService;
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
    private static final Logger logger = LogManager.getLogger(StoreController.class);

    @GetMapping("/index")
    public String showIndex(Model model) {
        return "index";
    }

    @GetMapping("/latest-additions")
    public String showLatestAdditions(Model model) {
        model.addAttribute("bookList", bookService.getLatestBooks());
        return "latest-additions";
    }

    @GetMapping("/bestsellers")
    public String showPopularBooks(Model model) {
        model.addAttribute("bookList", bookService.getPopularBooks());
        return "bestsellers";
    }

    @GetMapping("/notifications/{cid}")
    public String showNotificationsForCustomer(Model model, @PathVariable Long cid) {
        model.addAttribute("notificationList", customerService.getNotificationsByCustomerId(cid));
        return "notifications";
    }

}
