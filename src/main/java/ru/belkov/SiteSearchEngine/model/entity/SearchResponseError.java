package ru.belkov.SiteSearchEngine.model.entity;

import org.springframework.http.HttpStatus;

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
