package ru.belkov.SiteSearchEngine.model.entity;

public class SiteInformation {
    private String url;
    private String name;
    private String status;
    private String statusTime;
    private String error;
    private String pages;
    private String lemmas;

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

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
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
}
