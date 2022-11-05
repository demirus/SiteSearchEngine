package ru.belkov.SiteSearchEngine.services;

import org.jsoup.nodes.Document;
import ru.belkov.SiteSearchEngine.model.entity.Site;

public interface PageIndexService {
    boolean indexPage(String url);
    Document indexPage(String url, Site site);
}
