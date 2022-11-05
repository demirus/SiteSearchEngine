package ru.belkov.SiteSearchEngine.services;

import ru.belkov.SiteSearchEngine.model.entity.Page;

public interface PageService {
    boolean addIfNotExists(Page page);

    Page updateByPathAndSite(Page page);

    long count();

    Page getByUrl(String url);
}
