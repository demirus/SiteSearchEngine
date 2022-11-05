package ru.belkov.SiteSearchEngine.dto.search;

import org.springframework.http.HttpStatus;

public abstract class SearchResponse {
   private boolean result;

   private HttpStatus httpStatus;

    public SearchResponse(boolean result, HttpStatus httpStatus) {
        this.result = result;
        this.httpStatus = httpStatus;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
