package ru.belkov.SiteSearchEngine.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.belkov.SiteSearchEngine.dto.Response;
import ru.belkov.SiteSearchEngine.services.StatisticsService;

@RestController
public class StatisticsController {

    private StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("api/statistics")
    public ResponseEntity<Response> statistics() {
        Response response = statisticsService.getStatistics();
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}
