package ru.belkov.SiteSearchEngine.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DefaultController {
    @RequestMapping("/${web-interface.path}")
    public String index() {
        return "index";
    }
}
