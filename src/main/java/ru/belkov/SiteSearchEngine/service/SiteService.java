package ru.belkov.SiteSearchEngine.service;

import org.springframework.transaction.annotation.Transactional;
import ru.belkov.SiteSearchEngine.model.entity.Site;

import java.util.List;

public interface SiteService {
    Site addIfNotExists(Site site);

    Site updateSiteByUrl(Site site);
    List<Site> getAll();
    @Transactional
    void deleteSiteByUrl(Site site);
    @Transactional
    void deleteAll();
}
