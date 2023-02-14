package ru.belkov.SiteSearchEngine.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.belkov.SiteSearchEngine.dto.Response;
import ru.belkov.SiteSearchEngine.services.SearchService;

@RestController
public class SearchController {
    private SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("api/search")
    public ResponseEntity<Response> search(@RequestParam String query, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit, @RequestParam(required = false) String site) {
        Response response;
        if (site == null) {
            response = searchService.search(query, offset, limit);
        } else {
            response = searchService.search(query, site, offset, limit);
        }
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}
