package ru.belkov.SiteSearchEngine.model;

import ru.belkov.SiteSearchEngine.enums.SiteStatus;
import ru.belkov.SiteSearchEngine.model.entity.Site;
import ru.belkov.SiteSearchEngine.services.*;

public class SiteManagerImpl implements SiteManager {
    private SiteService siteService;
    private PageIndexService pageIndexService;
    private Site site;
    private Thread thread;
    private boolean stop = true;

    public SiteManagerImpl(Site site, SiteService siteService, PageIndexService pageIndexService) {
        this.site = site;
        this.siteService = siteService;
        this.pageIndexService = pageIndexService;
    }

    @Override
    public void startParsing() {
        stop = false;
        siteService.deleteSiteByUrl(site);
        site.setLastError("");
        site.setStatus(SiteStatus.INDEXING);
        site = siteService.addIfNotExists(site);
        SiteThread siteThread = new SiteThread(site, this, pageIndexService, siteService);
        thread = new Thread(siteThread);
        thread.start();
    }

    @Override
    public Site getSite() {
        return site;
    }

    @Override
    public void stopParsing() {
        stop = true;
        site.setStatus(SiteStatus.FAILED);
        site.setLastError("Индексация отменена");
        siteService.updateSiteByUrl(site);
    }

    @Override
    public boolean isStop() {
        return stop;
    }
}
