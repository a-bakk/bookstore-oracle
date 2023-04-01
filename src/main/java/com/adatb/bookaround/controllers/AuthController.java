package com.adatb.bookaround.controllers;

import com.adatb.bookaround.models.CustomerCreate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {
    @GetMapping("/auth")
    public String showAuth(Model model) {
        model.addAttribute("newUser", new CustomerCreate());
        return "auth";
    }

    @PostMapping("/register")
    public String register(CustomerCreate newUser) {
        //TODO: checks

        return "redirect:/auth";
    }

}
