package ru.belkov.SiteSearchEngine.model;

import ru.belkov.SiteSearchEngine.enums.SiteStatus;
import ru.belkov.SiteSearchEngine.model.entity.Site;
import ru.belkov.SiteSearchEngine.services.*;

import java.util.concurrent.ForkJoinPool;

public class SiteIndexingWorker implements Runnable {
    private Site site;
    private PageIndexService pageIndexService;
    private SiteManager siteManager;

    private SiteService siteService;

    public SiteIndexingWorker(Site site, SiteManager siteManager, PageIndexService pageIndexService, SiteService siteService) {
        this.site = site;
        this.pageIndexService = pageIndexService;
        this.siteManager = siteManager;
        this.siteService = siteService;
    }

    @Override
    public void run() {
        SiteParser siteParser = new SiteParser(site, site.getUrl(), siteManager, pageIndexService);
        ForkJoinPool.commonPool().invoke(siteParser);
        if (!siteManager.isStop()) {
            site.setStatus(SiteStatus.INDEXED);
            siteService.updateSiteByUrl(site);
        }
    }
}
