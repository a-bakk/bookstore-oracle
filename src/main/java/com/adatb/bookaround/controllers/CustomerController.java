package com.adatb.bookaround.controllers;

import com.adatb.bookaround.models.CustomerDetails;
import com.adatb.bookaround.models.ShoppingCart;
import com.adatb.bookaround.services.AuthService;
import com.adatb.bookaround.services.CustomerService;
import com.adatb.bookaround.services.StoreService;
import com.adatb.bookaround.services.constants.OrderMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

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
            return "redirect:/auth";
        model.addAttribute("cartItems", shoppingCart.getItems());
        model.addAttribute("cartSum", shoppingCart.calculateSum());
        model.addAttribute("customerDetails", currentCustomer);
        model.addAttribute("stores", storeService.getAllStores());
        return "place-order";
    }

    @GetMapping("/my-orders")
    public String showMyOrders(Model model, @AuthenticationPrincipal CustomerDetails customerDetails) {
        if (!AuthService.isAuthenticated())
            return "redirect:/auth";
        model.addAttribute("orderModels",
                customerService.getOrdersForCustomer(customerDetails.getCustomerId()));
        return "my-orders";
    }

    @PostMapping("/place-order-with-shipping")
    public String addOrderWithShipping(@SessionAttribute("shoppingCart") ShoppingCart shoppingCart,
                                       @AuthenticationPrincipal CustomerDetails customerDetails,
                                       RedirectAttributes redirectAttributes) throws Exception {
        if (customerService.createOrder(shoppingCart, customerDetails, OrderMode.SHIPPED_ORDER)) {
            redirectAttributes.addFlashAttribute("orderVerdict", "A rendelés sikeresen leadva!");
            shoppingCart.setItems(new ArrayList<>());
            return "redirect:/my-orders";
        }
        redirectAttributes.addFlashAttribute("orderVerdict", "A rendelés nem sikerült!");
        return "redirect:/my-orders";
    }

    @PostMapping("/place-order-with-pickup")
    public String addOrderWithPickup(@SessionAttribute("shoppingCart") ShoppingCart shoppingCart,
                                     @AuthenticationPrincipal CustomerDetails customerDetails,
                                     @RequestParam(name = "pickupStoreId") Long pickupStoreId,
                                     RedirectAttributes redirectAttributes) throws Exception {
        if (customerService.createOrder(shoppingCart, customerDetails, OrderMode.PICKUP_ORDER, pickupStoreId)) {
            redirectAttributes.addFlashAttribute("orderVerdict", "A rendelés sikeresen leadva!");
            shoppingCart.setItems(new ArrayList<>());
            return "redirect:/my-orders";
        }
        redirectAttributes.addFlashAttribute("orderVerdict", "A rendelés nem sikerült!");
        return "redirect:/my-orders";
    }

    @PostMapping("/invoices/pay-invoice/{id}")
    public String payInvoice(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!AuthService.isAuthenticated())
            return "redirect:/my-orders";
        // TODO check if invoice belongs to user
        redirectAttributes.addFlashAttribute("payVerdict",
                customerService.payInvoice(id)
                ? "A rendelés sikeresen ki lett fizetve."
                : "A fizetés sikertelen!");
        return "redirect:/my-orders";
    }

    @PostMapping("/orders/delete-order")
    public String deleteOrder(@RequestParam(name = "delete-order-id") Long orderId,
                              RedirectAttributes redirectAttributes) {
        if (!AuthService.isAuthenticated())
            return "redirect:/my-orders";
        redirectAttributes.addFlashAttribute("deleteOrderVerdict",
                customerService.deleteOrderById(orderId)
                ? "A rendelés sikeresen törölve!"
                : "A rendelés törlése sikertelen!");
        return "redirect:/my-orders";
    }

    @GetMapping("/profile")
    public String showProfile(Model model, @AuthenticationPrincipal CustomerDetails currentCustomer) {
        if (!AuthService.isAuthenticated())
            return "redirect:/auth";
        model.addAttribute("activePage", "profile");
        model.addAttribute("customerDetails", currentCustomer);
        return "profile";
    }
}
