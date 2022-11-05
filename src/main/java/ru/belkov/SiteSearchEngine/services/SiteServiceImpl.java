package ru.belkov.SiteSearchEngine.services;

import org.springframework.stereotype.Service;
import ru.belkov.SiteSearchEngine.model.entity.Site;
import ru.belkov.SiteSearchEngine.repository.SiteRepository;

import java.util.List;

@Service
public class SiteServiceImpl implements SiteService{
    private SiteRepository siteRepository;

    public SiteServiceImpl(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    @Override
    public synchronized Site addIfNotExists(Site site) {
        if (!siteRepository.existsByUrl(site.getUrl())) {
            return siteRepository.saveAndFlush(site);
        }
        return siteRepository.getByUrl(site.getUrl());
    }

    @Override
    public synchronized Site updateSiteByUrl(Site site) {
        Site dbSite = siteRepository.getByUrl(site.getUrl());
        dbSite.setUrl(site.getUrl());
        dbSite.setStatus(site.getStatus());
        dbSite.setStatusTime(site.getStatusTime());
        dbSite.setName(site.getName());
        dbSite.setLastError(site.getLastError());
        return siteRepository.saveAndFlush(dbSite);
    }

    @Override
    public List<Site> getAll() {
        return siteRepository.findAll();
    }

    @Override
    public void deleteSiteByUrl(Site site) {
        siteRepository.deleteByUrl(site.getUrl());
    }

    @Override
    public void deleteAll() {
        siteRepository.deleteAll();
    }

    @Override
    public long count() {
        return siteRepository.count();
    }

    @Override
    public Site getByUrl(String url) {
        return siteRepository.getByUrl(url);
    }
}
