package ru.belkov.SiteSearchEngine.service;

import ru.belkov.SiteSearchEngine.model.entity.SearchPage;
import ru.belkov.SiteSearchEngine.model.entity.Site;

import java.util.List;

public interface SearchService {
    List<SearchPage> search(String searchRequest);
    List<SearchPage> search(String searchRequest, Site site);
}
