package ru.belkov.SiteSearchEngine.service;

import ru.belkov.SiteSearchEngine.model.entity.Page;

public interface PageService {
    boolean addIfNotExists(Page page);

    Page updateByPath(Page page);
}
