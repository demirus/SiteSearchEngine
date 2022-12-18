package ru.belkov.SiteSearchEngine.services;

public interface SiteParserService {
    void parseSite(String name, String url);

    void loadSiteManagers();

    boolean isIndexing();

    boolean startParsing();

    boolean stopIndexing();

    void deleteSite(String url);

    void startParsing(String url);

    void stopParsing(String url);
}
