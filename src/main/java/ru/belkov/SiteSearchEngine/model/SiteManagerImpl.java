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
    private volatile boolean stop = true;
    private static final Logger logger = LoggerFactory.getLogger(SiteManagerImpl.class);

    public SiteManagerImpl(Site site, SiteService siteService, PageIndexService pageIndexService) {
        this.site = site;
        this.siteService = siteService;
        this.pageIndexService = pageIndexService;
    }

    @Override
    public synchronized void startParsing() {
        if (stop) {
            stop = false;
            if (site != null) {
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
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    @Override
    public synchronized void stopParsing() {
        if (!stop) {
            stop = true;
            if (site != null) {
                site.setStatus(SiteStatus.FAILED);
                site.setLastError("Индексация отменена");
                siteService.updateSiteByUrl(site);
            }
        }
    }

    @Override
    public boolean isStop() {
        return stop;
    }

    @Override
    public synchronized void deleteSite() {
        if (site != null) {
            stopParsing();
            try {
                if (thread != null && thread.isAlive()) {
                    thread.join();
                }
            } catch (Exception e) {
                logger.error(e.toString());
            }
            siteService.deleteSiteByUrl(site);
            site = null;
        }
    }

    @Override
    public synchronized void stopParsing(String error) {
        if (!stop) {
            stop = true;
            if (site != null) {
                site.setStatus(SiteStatus.FAILED);
                site.setLastError(error);
                siteService.updateSiteByUrl(site);
            }
        }
    }
}
