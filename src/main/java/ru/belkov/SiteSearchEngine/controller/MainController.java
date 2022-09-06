package ru.belkov.SiteSearchEngine.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class MainController {
    @RequestMapping("/")
    public String getIndex(){
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
