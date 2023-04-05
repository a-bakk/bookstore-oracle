package com.adatb.bookaround.controllers;

import com.adatb.bookaround.models.ShoppingCart;
import com.adatb.bookaround.services.BookService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@SessionAttributes("shoppingCart")
public class ShoppingCartController {
    @Autowired
    private BookService bookService;

    @ModelAttribute("shoppingCart")
    public ShoppingCart createShoppingCart() {
        return new ShoppingCart();
    }

    @GetMapping("/cart")
    public String showShoppingCart(Model model,
                                   @ModelAttribute("shoppingCart") ShoppingCart shoppingCart) {
        model.addAttribute("cartItems", shoppingCart.getItems());
        model.addAttribute("cartSum", shoppingCart.calculateSum());
        return "cart";
    }

    @PostMapping("/cart/addItemToCart")
    public String addItemToCart(@ModelAttribute("shoppingCart") ShoppingCart shoppingCart,
                                @RequestParam(name = "bookIdToAdd") Long bookId,
                                @RequestParam(name = "countToAdd") int count,
                                RedirectAttributes redirectAttributes) {
        shoppingCart.addItem(
                bookService.getEncapsulatedBook(bookId),
                count
        );
        redirectAttributes.addFlashAttribute("bookAdded", "A könyv hozzáadva a kosárhoz!");
        return "redirect:/book/" + bookId;
    }

    @PostMapping("/cart/removeItemFromCart")
    public String removeItemFromCart(@ModelAttribute("shoppingCart") ShoppingCart shoppingCart,
                                     @RequestParam(name = "bookIdToRemove") Long bookId,
                                     RedirectAttributes redirectAttributes) {
        shoppingCart.removeItem(bookService.getEncapsulatedBook(bookId));
        redirectAttributes.addFlashAttribute("bookRemoved", "A könyv eltávolítva a kosárból!");
        return "redirect:/cart";
    }

}
