package ru.belkov.SiteSearchEngine.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
    private Boolean result;
    private String error;
    @JsonIgnore
    private HttpStatus httpStatus;

    public Response(Boolean result, String error, HttpStatus httpStatus) {
        this.result = result;
        this.error = error;
        this.httpStatus = httpStatus;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
