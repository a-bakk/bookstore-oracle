package com.adatb.bookaround.controllers;

import com.adatb.bookaround.models.CustomerCreate;
import com.adatb.bookaround.models.CustomerDetails;
import com.adatb.bookaround.models.ShoppingCart;
import com.adatb.bookaround.repositories.CustomerDao;
import com.adatb.bookaround.services.AuthService;
import com.adatb.bookaround.services.CustomerService;
import com.adatb.bookaround.services.StoreService;
import com.adatb.bookaround.services.constants.OrderMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class CustomerController {

    private static final Logger logger = LogManager.getLogger(CustomerService.class);

    @Autowired
    private StoreService storeService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/place-order")
    public String showCreateOrder(Model model,
                                  @SessionAttribute("shoppingCart") ShoppingCart shoppingCart,
                                  @AuthenticationPrincipal CustomerDetails currentCustomer,
                                  RedirectAttributes redirectAttributes) {
        if (!AuthService.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("requiresAuthentication",
                    "A funkció eléréséhez előbb jelentkezzen be!");
            return "redirect:/auth";
        }
        model.addAttribute("cartItems", shoppingCart.getItems());
        model.addAttribute("cartSum", shoppingCart.calculateSum());
        model.addAttribute("customerDetails", currentCustomer);
        model.addAttribute("currentCustomer", currentCustomer);
        model.addAttribute("stores", storeService.getAllStores());
        return "place-order";
    }

    @GetMapping("/my-orders")
    public String showMyOrders(Model model, @AuthenticationPrincipal CustomerDetails customerDetails,
                               RedirectAttributes redirectAttributes) {
        if (!AuthService.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("requiresAuthentication",
                    "A funkció eléréséhez előbb jelentkezzen be!");
            return "redirect:/auth";
        }
        model.addAttribute("orderModels",
                customerService.getOrdersForCustomer(customerDetails.getCustomerId()));
        model.addAttribute("currentCustomer", customerDetails);
        model.addAttribute("activePage", "my-orders");
        return "my-orders";
    }

    @GetMapping("/wishlists")
    public String showWishlists(Model model, @AuthenticationPrincipal CustomerDetails customerDetails,
                                RedirectAttributes redirectAttributes) {
        if (!AuthService.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("requiresAuthentication",
                    "A funkció eléréséhez előbb jelentkezzen be!");
            return "redirect:/auth";
        }
        model.addAttribute("wishlistModels",
                customerService.getWishlistsForCustomer(customerDetails.getCustomerId()));
        model.addAttribute("currentCustomer", customerDetails);
        model.addAttribute("activePage", "wishlists");
        return "wishlists";
    }

    @PostMapping("/create-wishlist")
    public String addWishlist(@RequestParam(name = "wishlistName") String wishlistName,
                              @AuthenticationPrincipal CustomerDetails customerDetails,
                              RedirectAttributes redirectAttributes) {
        if (!AuthService.isAuthenticated())
            return "redirect:/index";
        redirectAttributes.addFlashAttribute("wishlistCreationVerdict",
                customerService.createWishlist(wishlistName, customerDetails.getCustomerId())
                        ? "Kívánságlista sikeresen létrehozva!"
                        : "Kívánságlista létrehozása sikertelen!");
        return "redirect:/wishlists";
    }

    @PostMapping("/modify-wishlist")
    public String modifyWishlist(@RequestParam(name = "modifyWishlistName") String wishListName,
                                 @RequestParam(name = "modifyWishlistId") Long wishlistId,
                                 RedirectAttributes redirectAttributes) {
        if (!AuthService.isAuthenticated())
            return "redirect:/index";
        redirectAttributes.addFlashAttribute("wishlistModificationVerdict",
                customerService.modifyWishlist(wishListName, wishlistId)
                        ? "Kívánságlista sikeresen módosítva!"
                        : "Kívánságlista módosítása sikertelen!");
        return "redirect:/wishlists";
    }

    @PostMapping("/delete-wishlist")
    public String deleteWishlist(@RequestParam(name = "deleteWishlistId") Long wishlistId,
                                 RedirectAttributes redirectAttributes) {
        if (!AuthService.isAuthenticated())
            return "redirect:/index";
        redirectAttributes.addFlashAttribute("wishlistDeletionVerdict",
                customerService.deleteWishlist(wishlistId)
                        ? "Kívánságlista sikeresen törölve!"
                        : "Kívánságlista törlése sikertelen!");
        return "redirect:/wishlists";
    }

    @PostMapping("/add-book-to-wishlist")
    public String addBookToWishlist(@RequestParam(name = "chosenWishlistId") Long wishlistId,
                                    @RequestParam(name = "bookIdToAddToWishlist") Long bookId,
                                    RedirectAttributes redirectAttributes) {
        if (!AuthService.isAuthenticated())
            return "redirect:/index";
        redirectAttributes.addFlashAttribute("addToWishlistVerdict",
                customerService.addBookToWishlist(wishlistId, bookId)
                        ? "A könyv sikeresen hozzáadva a kívánságlistához!"
                        : "A könyv hozzáadása sikertelen!");
        return "redirect:/wishlists";
    }

    @PostMapping("/remove-book-from-wishlist")
    public String removeBookFromWishlist(@RequestParam(name = "partOfBookId") Long bookId,
                                         @RequestParam(name = "partOfWishlistId") Long wishlistId,
                                         RedirectAttributes redirectAttributes) {
        if (!AuthService.isAuthenticated())
            return "redirect:/index";
        redirectAttributes.addFlashAttribute("removeFromWishlistVerdict",
                customerService.removeBookFromWishlist(wishlistId, bookId)
                        ? "Sikeresen törölve a könyv a kívánságlistáról!"
                        : "A könyv törlése sikertelen!");
        return "redirect:/wishlists";
    }

    @PostMapping("/delete-customer-by-id")
    public String deleteCustomerById(@RequestParam(name = "toDeleteId") Long customerId,
                                     RedirectAttributes redirectAttributes) {
        // needs to be changed if used for other than admin functionalities
        redirectAttributes.addFlashAttribute("customerDeletionVerdict",
                customerService.deleteCustomerById(customerId)
                        ? "Az ügyfél törlése sikeres!"
                        : "Az ügyfél törlése sikertelen!");
        return "redirect:/admin-panel";
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
    public String payInvoice(@PathVariable Long id, RedirectAttributes redirectAttributes,
                             @AuthenticationPrincipal CustomerDetails customerDetails) {
        if (!AuthService.isAuthenticated())
            return "redirect:/my-orders";

        if (customerService.seeIfInvoiceBelongsToCustomer(id, customerDetails.getCustomerId())) {
            return "redirect:/my-orders";
        }

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
    public String showProfile(Model model, @AuthenticationPrincipal CustomerDetails currentCustomer,
                              RedirectAttributes redirectAttributes) {
        if (!AuthService.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("requiresAuthentication",
                    "A funkció eléréséhez előbb jelentkezzen be!");
            return "redirect:/auth";
        }
        model.addAttribute("activePage", "profile");
        model.addAttribute("currentCustomer", currentCustomer);
        model.addAttribute("modifyCustomer", new CustomerCreate());
        return "profile";
    }

    @PostMapping("/modify-profile")
    public String modifyProfile(Long userId, String firstName, String lastName,
                                    String country, String stateOrRegion, String postcode,
                                    String city, String street, String password, String repassword,
                                    String oldPassword, RedirectAttributes redirectAttributes,
                                    @AuthenticationPrincipal CustomerDetails currentCustomer) {
        int errorCount = 0;
        List<String> modifyProfileErrorMessage = new ArrayList<>();
        String newPassword = null;


        //Empty fields
        if (firstName.isBlank() || lastName.isBlank() || country.isBlank() || stateOrRegion.isBlank() ||
                postcode.isBlank() || city.isBlank() || street.isBlank()) {
            modifyProfileErrorMessage.add("Egy mező sem lehet üresen hagyva! (ne használj szóközt se)");
            errorCount++;
        }


        //if modify password
        if (!password.isBlank() && !repassword.isBlank()) {
            //Invalid password format
            if (!AuthService.isValidPassword(password)) {
                modifyProfileErrorMessage.add("A jelszónak legalább 6 karakterből kell állnia!");
                errorCount++;
            }

            //Passwords missmatch
            if (!Objects.equals(password, repassword)) {
                modifyProfileErrorMessage.add("A jelszavaknak egyeznie kell!");
                errorCount++;
            } else {
                newPassword = bCryptPasswordEncoder.encode(password);
            }
        }

        if (!bCryptPasswordEncoder.matches(oldPassword, customerDao.find(userId).getPassword())) {
            modifyProfileErrorMessage.add("Rossz régi jelszót adtál meg!");
            errorCount++;
        }

        //If no mistakes found then modify
        if (errorCount == 0) {
            CustomerCreate modifiedCustomer = new CustomerCreate();
            modifiedCustomer.setFirstName(firstName);
            modifiedCustomer.setLastName(lastName);
            modifiedCustomer.setCountry(country);
            modifiedCustomer.setStateOrRegion(stateOrRegion);
            modifiedCustomer.setPostcode(postcode);
            modifiedCustomer.setCity(city);
            modifiedCustomer.setStreet(street);

            //update session profile stats
            currentCustomer.setFirstName(firstName);
            currentCustomer.setLastName(lastName);
            currentCustomer.setCountry(country);
            currentCustomer.setStateOrRegion(stateOrRegion);
            currentCustomer.setPostcode(postcode);
            currentCustomer.setCity(city);
            currentCustomer.setStreet(street);
            if (newPassword != null) {
                currentCustomer.setPassword(newPassword);
            }

            if (!customerService.modifyCustomerById(userId, modifiedCustomer, newPassword)) {
                modifyProfileErrorMessage.add("Valami hiba lépett fel a módosításban! Kérlek próbáld újra!");
                redirectAttributes.addFlashAttribute("modifyProfileErrorMessage", modifyProfileErrorMessage);
                return "redirect:/profile";
            }

            redirectAttributes.addFlashAttribute("modifyProfileMessage", "Sikeres módosítás!");
            return "redirect:/profile";
        }
        redirectAttributes.addFlashAttribute("modifyProfileErrorMessage", modifyProfileErrorMessage);

        return "redirect:/profile";
    }

    @PostMapping("delete-profile")
    public String deleteProfile(Long customerId) {
        customerService.deleteCustomerById(customerId);
        return "redirect:/logout";
    }
}
