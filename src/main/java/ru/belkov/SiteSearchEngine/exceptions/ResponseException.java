package ru.belkov.SiteSearchEngine.exceptions;

import org.springframework.http.HttpStatus;

public class ResponseException extends Exception {
    private HttpStatus httpStatus;
    public ResponseException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
