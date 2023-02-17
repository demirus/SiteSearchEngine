package ru.belkov.SiteSearchEngine.services;

import org.jsoup.nodes.Document;
import ru.belkov.SiteSearchEngine.dto.Response;
import ru.belkov.SiteSearchEngine.model.entity.Site;

import java.io.IOException;

public interface PageIndexService {
    Response indexPage(String url);
    Document indexPage(String url, Site site) throws IOException;
}
