package ru.belkov.SiteSearchEngine.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.belkov.SiteSearchEngine.services.PageIndexService;
import ru.belkov.SiteSearchEngine.services.SiteParserService;
import ru.belkov.SiteSearchEngine.services.SiteParserServiceImpl;

import java.util.HashMap;
import java.util.Map;

@RestController
public class IndexingController {
    private SiteParserService siteParserService;
    private PageIndexService pageIndexService;


    public IndexingController(SiteParserServiceImpl siteParserService, PageIndexService pageIndexService) {
        this.siteParserService = siteParserService;
        this.pageIndexService = pageIndexService;
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<Map<String, String>> startIndexing() {
        ResponseEntity<Map<String, String>> response;
        if (siteParserService.isIndexing()) {
            Map<String, String> map = new HashMap<>();
            map.put("result", "false");
            map.put("error", "Индексация уже запущена");
            response = new ResponseEntity<>(map, HttpStatus.OK);
        } else {
            siteParserService.startParsing();
            Map<String, String> map = new HashMap<>();
            map.put("result", "true");
            response = new ResponseEntity<>(map, HttpStatus.OK);
        }
        return response;
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<Map<String, String>> stopIndexing() {
        ResponseEntity<Map<String, String>> response;
        if (!siteParserService.stopIndexing()) {
            Map<String, String> map = new HashMap<>();
            map.put("result", "false");
            map.put("error", "Индексация не запущена");
            response = new ResponseEntity<>(map, HttpStatus.OK);
        } else {
            Map<String, String> map = new HashMap<>();
            map.put("result", "true");
            response = new ResponseEntity<>(map, HttpStatus.OK);
        }
        return response;
    }

    @GetMapping("/stopSiteIndexing")
    public Map<String, Object> stopSiteIndexing(@RequestParam String url) {
        Map<String, Object> answer = new HashMap<>();
        siteParserService.stopParsing(url);
        return answer;
    }

    @GetMapping("/startSiteIndexing")
    public Map<String, Object> startSiteIndexing(@RequestParam String url) {
        Map<String, Object> answer = new HashMap<>();
        siteParserService.startParsing(url);
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
