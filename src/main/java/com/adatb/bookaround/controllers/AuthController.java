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
    public String register(CustomerCreate newUser) {
        if (this.customerDao.findByEmail(newUser.getEmail()) != null) {
            //error message: email occupied
            //redirectAttributes.addFlashAttribute("fail", "Another user with this email address already exists!");
            logger.warn("Registration error: occupied email");
            return "redirect:/auth";
        }

        if (AuthService.isValidEmail(newUser.getEmail()) && AuthService.isValidPassword(newUser.getPassword()) && Objects.equals(newUser.getPassword(), newUser.getRepassword())) {
            this.authService.register(newUser);
            //success message: registration successful
            //redirectAttributes.addFlashAttribute("success", "Registration successful! You can now sign in.");
            logger.warn("Registration successful");
            return "redirect:/auth";
        }

        logger.warn("Error: Registrated user's email: " + newUser.getEmail());
        //error message: any errors occured
        //redirectAttributes.addFlashAttribute("fail", "There has been an issue during registration. Please try again!");
        return "redirect:/auth";
    }

}
