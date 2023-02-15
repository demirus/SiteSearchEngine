package ru.belkov.SiteSearchEngine.dto.statistics;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SiteInformation {
    private String url;
    private String name;
    private String status;
    private String statusTime;
    private String pages;
    private String lemmas;
    private String error;

    public SiteInformation() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusTime() {
        return statusTime;
    }

    public void setStatusTime(String statusTime) {
        this.statusTime = statusTime;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getLemmas() {
        return lemmas;
    }

    public void setLemmas(String lemmas) {
        this.lemmas = lemmas;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
