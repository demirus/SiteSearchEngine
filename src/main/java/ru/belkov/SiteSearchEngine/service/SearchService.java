package ru.belkov.SiteSearchEngine.service;

import ru.belkov.SiteSearchEngine.model.entity.SearchResponse;
import ru.belkov.SiteSearchEngine.model.entity.Site;

public interface SearchService {
    SearchResponse search(String searchRequest);
    SearchResponse search(String searchRequest, Site site);
}
