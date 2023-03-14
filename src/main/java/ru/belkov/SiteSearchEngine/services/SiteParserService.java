package ru.belkov.SiteSearchEngine.services;

import ru.belkov.SiteSearchEngine.dto.Response;

public interface SiteParserService {
    Response parseSite(String name, String url);

    void loadSiteManagers();

    boolean isFullIndexing();

    boolean isFullNotIndexing();

    Response startParsing();

    Response stopIndexing();

    Response deleteSite(String url);

    Response startSiteParsing(String url);

    Response stopSiteParsing(String url);
}
