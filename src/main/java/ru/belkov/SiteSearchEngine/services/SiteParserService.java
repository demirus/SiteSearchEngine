package ru.belkov.SiteSearchEngine.services;

import ru.belkov.SiteSearchEngine.exceptions.ResponseException;

public interface SiteParserService {
    void parseSite(String name, String url);

    void loadSiteManagers();

    boolean isFullIndexing();

    boolean isFullNotIndexing();

    boolean startParsing();

    boolean stopIndexing();

    void deleteSite(String url) throws ResponseException;

    void startParsing(String url) throws ResponseException;

    void stopParsing(String url) throws ResponseException;
}
