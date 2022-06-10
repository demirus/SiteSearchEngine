package ru.belkov.SiteSearchEngine.model.entity;

public class SearchPage {
    public String uri;

    public String title;

    public String snippet;

    public Double relevance;

    public SearchPage() {
    }

    public SearchPage(String uri, String title, String snippet, Double relevance) {
        this.uri = uri;
        this.title = title;
        this.snippet = snippet;
        this.relevance = relevance;
    }
}
