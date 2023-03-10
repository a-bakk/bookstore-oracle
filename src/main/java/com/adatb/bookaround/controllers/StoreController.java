package com.adatb.bookaround.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class StoreController {

    public String showIndex(Model model) {
        return "index";
    }

}
