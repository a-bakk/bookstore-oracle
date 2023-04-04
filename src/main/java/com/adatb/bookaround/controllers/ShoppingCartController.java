package com.adatb.bookaround.controllers;

import com.adatb.bookaround.models.ShoppingCart;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes("shoppingCart")
public class ShoppingCartController {
    private static final Logger logger = LogManager.getLogger(ShoppingCartController.class);
    @ModelAttribute("shoppingCart")
    public ShoppingCart createShoppingCart() {
        return new ShoppingCart();
    }

    @GetMapping("/cart")
    public String showShoppingCart(Model model,
                                   @ModelAttribute("shoppingCart") ShoppingCart shoppingCart) {
        model.addAttribute("cart", shoppingCart);
        return "cart";
    }

}
