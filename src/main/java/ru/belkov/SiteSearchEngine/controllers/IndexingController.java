package ru.belkov.SiteSearchEngine.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.belkov.SiteSearchEngine.services.PageIndexService;
import ru.belkov.SiteSearchEngine.services.SiteParserServiceImpl;

import java.util.HashMap;
import java.util.Map;

@RestController
public class IndexingController {
    private SiteParserServiceImpl siteParserService;
    private PageIndexService pageIndexService;


    public IndexingController(SiteParserServiceImpl siteParserService, PageIndexService pageIndexService) {
        this.siteParserService = siteParserService;
        this.pageIndexService = pageIndexService;
    }

    @GetMapping("/startIndexing")
    public Map<String, Object> startIndexing() {
        Map<String, Object> answer = new HashMap<>();
        if (siteParserService.isIndexing()) {
            answer.put("result", false);
            answer.put("error", "Индексация уже запущена");
        } else {
            siteParserService.startParsing();
            answer.put("result", true);
        }
        return answer;
    }

    @GetMapping("/stopIndexing")
    public Map<String, Object> stopIndexing() {
        Map<String, Object> answer = new HashMap<>();
        if (!siteParserService.stopIndexing()) {
            answer.put("result", false);
            answer.put("error", "Индексация не запущена");
        } else {
            answer.put("result", true);
        }
        return answer;
    }

    @PostMapping("/indexPage")
    public Map<String, Object> indexPage(@RequestParam String url) {
        Map<String, Object> answer = new HashMap<>();
        if (pageIndexService.indexPage(url)) {
            answer.put("result", true);
        } else {
            answer.put("result", false);
            answer.put("error", "Данная страница находится за пределами сайтов, указанных в конфигурационном файле");
        }
        return answer;
    }

    @PostMapping("/indexSite")
    public Map<String, Object> indexPage(@RequestParam String name, @RequestParam String url) {
        siteParserService.parseSite(name, url);
        Map<String, Object> answer = new HashMap<>();
        answer.put("result", true);
        return answer;
    }

    @PostMapping("/deleteSite")
    public Map<String, Object> deleteSite(@RequestParam String url) {
        siteParserService.deleteSite(url);
        Map<String, Object> answer = new HashMap<>();
        answer.put("result", true);
        return answer;
    }
}
