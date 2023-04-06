package com.adatb.bookaround.controllers;

import com.adatb.bookaround.models.CustomerDetails;
import com.adatb.bookaround.models.ShoppingCart;
import com.adatb.bookaround.services.AuthService;
import com.adatb.bookaround.services.CustomerService;
import com.adatb.bookaround.services.StoreService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
public class CustomerController {

    private static final Logger logger = LogManager.getLogger(CustomerController.class);

    @Autowired
    private StoreService storeService;

    @Autowired
    private CustomerService customerService;

    @GetMapping("/place-order")
    public String showCreateOrder(Model model,
                                  @SessionAttribute("shoppingCart") ShoppingCart shoppingCart,
                                  @AuthenticationPrincipal CustomerDetails currentCustomer) {
        if (!AuthService.isAuthenticated())
            return "redirect:/index";
        model.addAttribute("cartItems", shoppingCart.getItems());
        model.addAttribute("cartSum", shoppingCart.calculateSum());
        model.addAttribute("customerDetails", currentCustomer);
        model.addAttribute("stores", storeService.getAllStores());
        return "place-order";
    }

    @GetMapping("/my-orders")
    public String showMyOrders(Model model) {
        return "my-orders";
    }

    @PostMapping("/place-order-with-shipping")
    public String addOrderWithShipping(@SessionAttribute("shoppingCart") ShoppingCart shoppingCart,
                                          @AuthenticationPrincipal CustomerDetails customerDetails) {
        // TODO: additional checks here & in service
        customerService.createOrderWithShipping(shoppingCart, customerDetails);
        return "redirect:/my-orders";
    }

}
