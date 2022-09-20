package ru.belkov.SiteSearchEngine.controller;

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
    public SearchResponse search(@RequestParam String query, @RequestParam String offset, @RequestParam String limit, @RequestParam(required = false) String site) {
        if (site == null) {
            return searchService.search(query);
        } else {
            Site searchedSite = siteService.getByUrl(site);
            if (site != null) {
                return searchService.search(query, searchedSite);
            } else {
                return null;
            }
        }
    }
}
