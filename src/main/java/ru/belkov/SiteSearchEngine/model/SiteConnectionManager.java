package ru.belkov.SiteSearchEngine.model;

import org.jsoup.Connection;
import ru.belkov.SiteSearchEngine.config.SiteParserConfig;

public interface SiteConnectionManager {
    Connection.Response getResponse(String url, SiteParserConfig siteParserConfig);
}
