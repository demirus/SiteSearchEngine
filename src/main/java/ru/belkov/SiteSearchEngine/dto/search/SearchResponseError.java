package ru.belkov.SiteSearchEngine.dto.search;

import org.springframework.http.HttpStatus;
import ru.belkov.SiteSearchEngine.dto.search.SearchResponse;

public class SearchResponseError extends SearchResponse {
    private String error;

    public SearchResponseError(boolean result, String error, HttpStatus httpStatus) {
        super(result, httpStatus);
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
