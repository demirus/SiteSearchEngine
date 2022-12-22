package ru.belkov.SiteSearchEngine.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.belkov.SiteSearchEngine.enums.SiteStatus;
import ru.belkov.SiteSearchEngine.model.entity.Site;
import ru.belkov.SiteSearchEngine.services.*;

public class SiteManagerImpl implements SiteManager {
    private SiteService siteService;
    private PageIndexService pageIndexService;
    private Site site;
    private Thread thread;
    private boolean stop = true;
    private static final Logger logger = LoggerFactory.getLogger(SiteManagerImpl.class);

    public SiteManagerImpl(Site site, SiteService siteService, PageIndexService pageIndexService) {
        this.site = site;
        this.siteService = siteService;
        this.pageIndexService = pageIndexService;
    }

    @Override
    public void startParsing() {
        try {
            if (thread != null && thread.isAlive()) {
                thread.join();
            }
        } catch (Exception e) {
            logger.error(e.toString());
        }
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
        try {
            if (thread != null && thread.isAlive()) {
                thread.join();
            }
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    @Override
    public boolean isStop() {
        return stop;
    }
}
