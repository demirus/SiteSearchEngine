package ru.belkov.SiteSearchEngine.dto.statistics;

import org.springframework.http.HttpStatus;
import ru.belkov.SiteSearchEngine.dto.Response;

public class StatisticsResponse extends Response {
    private Statistics statistics;

    public StatisticsResponse(Boolean result, String error, HttpStatus httpStatus) {
        super(result, error, httpStatus);
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }
}
