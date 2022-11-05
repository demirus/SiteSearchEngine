package ru.belkov.SiteSearchEngine.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class DefaultController {
    @RequestMapping("/")
    public String getIndex() {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @RequestMapping("/${web-interface.path}")
    public String index() {
        return "index";
    }
}
