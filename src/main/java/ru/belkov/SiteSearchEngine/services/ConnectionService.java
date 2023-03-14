package ru.belkov.SiteSearchEngine.services;

import org.jsoup.Connection;
import ru.belkov.SiteSearchEngine.model.entity.Site;

public interface ConnectionService {
    Connection.Response getResponse(Site site, String url);
}
