package ru.belkov.SiteSearchEngine.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.belkov.SiteSearchEngine.service.SiteParserService;

import java.util.HashMap;
import java.util.Map;

@RestController
public class IndexingController {
    SiteParserService siteParserService;

    public IndexingController(SiteParserService siteParserService) {
        this.siteParserService = siteParserService;
    }

    @GetMapping("/startIndexing")
    public Map<String, String> startIndexing() {
        Map<String, String> answer = new HashMap<>();
        if (siteParserService.isIndexing()) {
            answer.put("result", "false");
            answer.put("error", "Индексация уже запущена");
        } else {
            siteParserService.startParsing();
            answer.put("result", "true");
        }
        return answer;
    }

    @GetMapping("/stopIndexing")
    public Map<String, String> stopIndexing() {
        Map<String, String> answer = new HashMap<>();
        if (!siteParserService.stopIndexing()) {
            answer.put("result", "false");
            answer.put("error", "Индексация не запущена");
        } else {
            answer.put("result", "true");
        }
        return answer;
    }
}
