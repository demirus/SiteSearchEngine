package ru.belkov.SiteSearchEngine.service;

import ru.belkov.SiteSearchEngine.model.entity.Site;

public interface SiteService {
    Site addIfNotExists(Site site);

    Site updateSiteByUrl(Site site);
}
