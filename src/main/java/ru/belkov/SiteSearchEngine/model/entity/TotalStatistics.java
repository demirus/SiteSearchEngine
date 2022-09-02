package ru.belkov.SiteSearchEngine.model.entity;

public class TotalStatistics {
    private long sites;
    private long pages;
    private long lemmas;
    private boolean isIndexing;

    public long getSites() {
        return sites;
    }

    public void setSites(long sites) {
        this.sites = sites;
    }

    public long getPages() {
        return pages;
    }

    public void setPages(long pages) {
        this.pages = pages;
    }

    public long getLemmas() {
        return lemmas;
    }

    public void setLemmas(long lemmas) {
        this.lemmas = lemmas;
    }

    public boolean getIsIndexing() {
        return isIndexing;
    }

    public void setIsIndexing(boolean indexing) {
        isIndexing = indexing;
    }
}
