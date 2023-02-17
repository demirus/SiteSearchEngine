package ru.belkov.SiteSearchEngine.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.belkov.SiteSearchEngine.dto.Response;
import ru.belkov.SiteSearchEngine.services.PageIndexService;
import ru.belkov.SiteSearchEngine.services.SiteParserService;
import ru.belkov.SiteSearchEngine.services.impl.SiteParserServiceImpl;

@RestController
public class IndexingController {
    private SiteParserService siteParserService;
    private PageIndexService pageIndexService;

    public IndexingController(SiteParserServiceImpl siteParserService, PageIndexService pageIndexService) {
        this.siteParserService = siteParserService;
        this.pageIndexService = pageIndexService;
    }

    @GetMapping("api/startIndexing")
    public Response startIndexing() {
        return siteParserService.startParsing();
    }

    @GetMapping("api/stopIndexing")
    public Response stopIndexing() {
        return siteParserService.stopIndexing();
    }

    @GetMapping("api/stopSiteIndexing")
    public Response stopSiteIndexing(@RequestParam String url) {
        return siteParserService.stopSiteParsing(url);
    }

    @GetMapping("api/startSiteIndexing")
    public Response startSiteIndexing(@RequestParam String url) {
        return siteParserService.startSiteParsing(url);
    }

    @PostMapping("api/indexPage")
    public Response indexPage(@RequestParam String url) {
        return pageIndexService.indexPage(url);
    }

    @PostMapping("api/indexSite")
    public Response indexSite(@RequestParam String name, @RequestParam String url) {
        return siteParserService.parseSite(name, url);
    }

    @DeleteMapping("api/deleteSite")
    public Response deleteSite(@RequestParam String url) {
        return siteParserService.deleteSite(url);
    }
}
