package ru.belkov.SiteSearchEngine.services;

import ru.belkov.SiteSearchEngine.dto.Response;
import ru.belkov.SiteSearchEngine.exceptions.ResponseException;
import ru.belkov.SiteSearchEngine.model.entity.Site;

public interface SearchService {
    Response search(String searchRequest, Integer offset, Integer limit);
    Response search(String searchRequest, String siteUrl, Integer offset, Integer limit);
}
