package ru.belkov.SiteSearchEngine.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.belkov.SiteSearchEngine.model.entity.SearchResponse;
import ru.belkov.SiteSearchEngine.model.entity.Site;
import ru.belkov.SiteSearchEngine.service.SearchService;
import ru.belkov.SiteSearchEngine.service.SiteService;

@RestController
public class SearchController {
    private SearchService searchService;
    private SiteService siteService;

    public SearchController(SearchService searchService, SiteService siteService) {
        this.searchService = searchService;
        this.siteService = siteService;
    }

    @GetMapping("/search")
    public ResponseEntity<SearchResponse> search(@RequestParam String query, @RequestParam String offset, @RequestParam String limit, @RequestParam(required = false) String site) {
        SearchResponse searchResponse;
        if (site == null) {
            searchResponse = searchService.search(query);
            return new ResponseEntity<>(searchResponse, searchResponse.getHttpStatus());
        } else {
            Site searchedSite = siteService.getByUrl(site);
            if (searchedSite != null) {
                searchResponse = searchService.search(query, searchedSite);
                return new ResponseEntity<>(searchResponse, searchResponse.getHttpStatus());
            } else {
                return null;
            }
        }
    }
}
