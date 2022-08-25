package ru.belkov.SiteSearchEngine.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.belkov.SiteSearchEngine.service.StatisticsService;

import java.util.HashMap;
import java.util.Map;

@RestController
public class StatisticsController {

    private StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/statistics")
    public Map<String, Object> statistics() {
        Map<String, Object> result = new HashMap<>();
        result.put("result", "true");
        result.put("statistics", statisticsService.getStatistics());
        return result;
    }
}
