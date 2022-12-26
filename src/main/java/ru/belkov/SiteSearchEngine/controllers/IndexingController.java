package ru.belkov.SiteSearchEngine.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.belkov.SiteSearchEngine.exceptions.ResponseException;
import ru.belkov.SiteSearchEngine.model.SiteManagerImpl;
import ru.belkov.SiteSearchEngine.services.PageIndexService;
import ru.belkov.SiteSearchEngine.services.SiteParserService;
import ru.belkov.SiteSearchEngine.services.SiteParserServiceImpl;

import java.util.HashMap;
import java.util.Map;

@RestController
public class IndexingController {
    private SiteParserService siteParserService;
    private PageIndexService pageIndexService;
    private static final Logger logger = LoggerFactory.getLogger(SiteManagerImpl.class);


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
    public ResponseEntity<Map<String, String>> indexPage(@RequestParam String url) {
        try {
            pageIndexService.indexPage(url);
            Map<String, String> answer = formSuccessAnswer();
            return new ResponseEntity<>(answer, HttpStatus.OK);
        } catch (ResponseException e) {
            logger.error(e.getMessage(), e);
            Map<String, String> answer = formFailureAnswer();
            answer.put("error", e.getMessage());
            return new ResponseEntity<>(answer, e.getHttpStatus());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            Map<String, String> answer = formFailureAnswer();
            answer.put("error", "Непредвиденная ошибка сервера");
            return new ResponseEntity<>(answer, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/indexSite")
    public ResponseEntity<Map<String, String>> indexSite(@RequestParam String name, @RequestParam String url) {
        try {
            siteParserService.parseSite(name, url);
            Map<String, String> answer = formSuccessAnswer();
            return new ResponseEntity<>(answer, HttpStatus.OK);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            Map<String, String> answer = formFailureAnswer();
            answer.put("error", "Непредвиденная ошибка сервера");
            return new ResponseEntity<>(answer, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deleteSite")
    public ResponseEntity<Map<String, String>> deleteSite(@RequestParam String url) {
        try {
            siteParserService.deleteSite(url);
            Map<String, String> answer = formSuccessAnswer();
            return new ResponseEntity<>(answer, HttpStatus.OK);
        } catch (ResponseException e) {
            logger.error(e.getMessage(), e);
            Map<String, String> answer = formFailureAnswer();
            answer.put("error", e.getMessage());
            return new ResponseEntity<>(answer, e.getHttpStatus());
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            Map<String, String> answer = formFailureAnswer();
            answer.put("error", "Непредвиденная ошибка сервера");
            return new ResponseEntity<>(answer, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Map<String, String> formFailureAnswer() {
        Map<String, String> answer = new HashMap<>();
        answer.put("result", "false");
        return answer;
    }

    private Map<String, String> formSuccessAnswer() {
        Map<String, String> answer = new HashMap<>();
        answer.put("result", "true");
        return answer;
    }
}
