package ru.belkov.SiteSearchEngine.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.belkov.SiteSearchEngine.enums.SiteStatus;
import ru.belkov.SiteSearchEngine.model.SiteManager;
import ru.belkov.SiteSearchEngine.model.SiteManagerImpl;
import ru.belkov.SiteSearchEngine.model.entity.Site;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SiteParserServiceImpl implements SiteParserService {
    private final SiteService siteService;

    private final PageIndexService pageIndexService;

    private List<SiteManager> siteManagers = new ArrayList<>();

    @Autowired
    public SiteParserServiceImpl(SiteService siteService, PageIndexService pageIndexService) {
        this.siteService = siteService;
        this.pageIndexService = pageIndexService;
    }

    @Override
    public void parseSite(String name, String url) {
        SiteManager siteManager = getSiteManager(url);

        if (siteManager == null) {
            Site site = new Site();
            site.setName(name);
            site.setUrl(url);
            siteManager = new SiteManagerImpl(site, siteService, pageIndexService);
        }

        siteManager.startParsing();
    }

    @EventListener(ContextRefreshedEvent.class)
    public void initSiteManagers(ContextRefreshedEvent event) {
        SiteParserService siteParserService = event.getApplicationContext().getBean(SiteParserService.class);
        siteParserService.loadSiteManagers();
        siteParserService.startParsing();
    }

    @Override
    public void loadSiteManagers() {
        if (siteManagers.isEmpty()) {
            List<Site> sites = siteService.getAll();
            sites.forEach(s -> {
                SiteManagerImpl siteManager = new SiteManagerImpl(s, siteService, pageIndexService);
                siteManagers.add(siteManager);
            });
        }
    }

    @Override
    public boolean isIndexing() {
        return siteManagers.stream().noneMatch(SiteManager::isStop);
    }

    @Override
    public boolean startParsing() {
        if (isIndexing()) {
            return true;
        }
        siteManagers.forEach(s -> {
            if (s.isStop()) {
                s.startParsing();
            }
        });
        return false;
    }

    @Override
    public boolean stopIndexing() {
        if (!isIndexing()) {
            return false;
        }
        siteManagers.forEach(s -> {
            if (!s.isStop()) {
                s.stopParsing();
            }
        });
        return true;
    }

    @Override
    public void deleteSite(String url) {
        SiteManager siteManager = getSiteManager(url);
        if (siteManager != null) {
            siteService.deleteSiteByUrl(siteManager.getSite());
        }
    }

    private SiteManager getSiteManager(String url) {
        Optional<SiteManager> optionalSiteManager = siteManagers.stream().filter(sm -> sm.getSite() != null && sm.getSite().getUrl().equals(url)).findFirst();
        return optionalSiteManager.orElse(null);
    }
}
