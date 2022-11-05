package ru.belkov.SiteSearchEngine.services;

import ru.belkov.SiteSearchEngine.dto.search.SearchResponse;
import ru.belkov.SiteSearchEngine.model.entity.Site;

public interface SearchService {
    SearchResponse search(String searchRequest);
    SearchResponse search(String searchRequest, Site site);
}
