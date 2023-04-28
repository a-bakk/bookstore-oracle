package com.adatb.bookaround.controllers;

import com.adatb.bookaround.models.CustomerCreate;
import com.adatb.bookaround.models.CustomerDetails;
import com.adatb.bookaround.repositories.CustomerDao;
import com.adatb.bookaround.services.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class AuthController {

    private static final Logger logger = LogManager.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private CustomerDao customerDao;


    @GetMapping("/auth")
    public String showAuth(Model model, @AuthenticationPrincipal CustomerDetails customerDetails) {
        model.addAttribute("newUser", new CustomerCreate());
        model.addAttribute("activePage", "auth");
        model.addAttribute("currentCustomer", customerDetails);
        return "auth";
    }

    @PostMapping("/register")
    public String register(CustomerCreate newUser, RedirectAttributes redirectAttributes) {
        int errorCount = 0;
        List<String> registerErrorMessage = new ArrayList<>();

        //Empty fields
        if (newUser.getEmail().trim().equals("") || newUser.getFirstName().trim().equals("") ||
                newUser.getLastName().trim().equals("") || newUser.getCountry().trim().equals("") ||
                newUser.getStateOrRegion().trim().equals("") || newUser.getPostcode().trim().equals("") ||
                newUser.getCity().trim().equals("") || newUser.getStreet().trim().equals("")) {
            registerErrorMessage.add("Egy mező sem lehet üresen hagyva! (ne használj szóközt se)");
            errorCount++;
        }

        //Email occupied
        if (this.customerDao.findByEmail(newUser.getEmail()) != null) {
            registerErrorMessage.add("Email cím foglalt!");
            errorCount++;
        }

        //Invalid email format
        if (!AuthService.isValidEmail(newUser.getEmail())) {
            registerErrorMessage.add("Helytelen email cím!");
            errorCount++;
        }

        //Invalid password format
        if (!AuthService.isValidPassword(newUser.getPassword())) {
            registerErrorMessage.add("A jelszónak legalább 6 karakterből kell állnia!");
            errorCount++;
        }

        //Passwords missmatch
        if (!Objects.equals(newUser.getPassword(), newUser.getRepassword())) {
            registerErrorMessage.add("A jelszavaknak egyeznie kell!");
            errorCount++;
        }


        //If no mistakes found then register
        if (errorCount == 0) {
            this.authService.register(newUser);
            redirectAttributes.addFlashAttribute("registerMessage", "Sikeres regisztráció!");
            return "redirect:/auth";
        }

        redirectAttributes.addFlashAttribute("registerErrorMessage", registerErrorMessage);


        return "redirect:/auth";
    }

}
