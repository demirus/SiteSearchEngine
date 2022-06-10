package ru.belkov.SiteSearchEngine.service;

import ru.belkov.SiteSearchEngine.model.entity.SearchPage;

import java.util.List;

public interface SearchService {
    List<SearchPage> search(String searchRequest);
}
