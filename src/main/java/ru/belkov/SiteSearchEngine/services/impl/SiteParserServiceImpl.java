package ru.belkov.SiteSearchEngine.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.belkov.SiteSearchEngine.config.SiteParserConfig;
import ru.belkov.SiteSearchEngine.dto.Response;
import ru.belkov.SiteSearchEngine.model.SiteManager;
import ru.belkov.SiteSearchEngine.model.SiteManagerImpl;
import ru.belkov.SiteSearchEngine.model.StartParsingWorker;
import ru.belkov.SiteSearchEngine.model.entity.Site;
import ru.belkov.SiteSearchEngine.services.PageIndexService;
import ru.belkov.SiteSearchEngine.services.SiteParserService;
import ru.belkov.SiteSearchEngine.services.SiteService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class SiteParserServiceImpl implements SiteParserService {
    private final SiteService siteService;
    private final PageIndexService pageIndexService;
    private List<SiteManager> siteManagers = new ArrayList<>();
    private final Logger logger = LoggerFactory.getLogger(SiteParserServiceImpl.class);

    private final SiteParserConfig siteParserConfig;

    @Autowired
    public SiteParserServiceImpl(SiteService siteService, PageIndexService pageIndexService, SiteParserConfig siteParserConfig) {
        this.siteService = siteService;
        this.pageIndexService = pageIndexService;
        this.siteParserConfig = siteParserConfig;
    }

    @Override
    public synchronized Response parseSite(String name, String url) {
        try {
            SiteManager siteManager = getSiteManager(url);

            if (siteManager == null) {
                Site site = new Site();
                site.setName(name);
                site.setUrl(url);
                siteManager = new SiteManagerImpl(site, siteService, pageIndexService);
            }

            siteManager.startParsing();
            siteManagers.add(siteManager);
            return new Response(Boolean.TRUE, null, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new Response(Boolean.FALSE, "Непредвиденная ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @EventListener(ContextRefreshedEvent.class)
    public void initSiteManagers(ContextRefreshedEvent event) {
        if (siteParserConfig.isAutoStart()) {
            SiteParserService siteParserService = event.getApplicationContext().getBean(SiteParserService.class);
            siteParserService.loadSiteManagers();
            siteParserService.startParsing();
        }
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
    public boolean isFullIndexing() {
        return siteManagers.stream().noneMatch(SiteManager::isStop);
    }

    @Override
    public Response startParsing() {
        try {
            if (isFullIndexing()) {
                return new Response(Boolean.FALSE, "Индексация уже запущена", HttpStatus.BAD_REQUEST);
            } else {
                startParsing(siteManagers);
                return new Response(Boolean.TRUE, null, HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new Response(Boolean.FALSE, "Непредвиденная ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public boolean isFullNotIndexing() {
        return siteManagers.stream().allMatch(SiteManager::isStop);
    }

    @Override
    public Response stopIndexing() {
        try {
            if (!isFullNotIndexing()) {
                siteManagers.forEach(s -> {
                    if (!s.isStop()) {
                        s.stopParsing();
                    }
                });
                return new Response(Boolean.TRUE, null, HttpStatus.OK);
            } else {
                return new Response(Boolean.FALSE, "Индексация не запущена", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new Response(Boolean.FALSE, "Непредвиденная ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Response deleteSite(String url) {
        try {
            SiteManager siteManager = getSiteManager(url);
            if (siteManager != null) {
                siteManager.deleteSite();
                return new Response(Boolean.TRUE, null, HttpStatus.OK);
            } else {
                return new Response(Boolean.FALSE, "Данный сайт отсутствует в системе", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new Response(Boolean.FALSE, "Непредвиденная ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private SiteManager getSiteManager(String url) {
        Optional<SiteManager> optionalSiteManager = siteManagers.stream().filter(sm -> sm.getSite() != null && sm.getSite().getUrl().equals(url)).findFirst();
        return optionalSiteManager.orElse(null);
    }

    @Override
    public Response startSiteParsing(String url) {
        try {
            SiteManager siteManager = getSiteManager(url);
            if (siteManager != null) {
                if (!siteManager.isStop()) {
                    return new Response(Boolean.FALSE, "Индексация сайта уже запущена", HttpStatus.BAD_REQUEST);
                } else {
                    startParsing(Collections.singletonList(siteManager));
                    return new Response(Boolean.TRUE, null, HttpStatus.OK);
                }
            } else {
                return new Response(Boolean.FALSE, "Данный сайт отсутствует в системе", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new Response(Boolean.FALSE, "Непредвиденная ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Response stopSiteParsing(String url) {
        try {
            SiteManager siteManager = getSiteManager(url);
            if (siteManager != null) {
                if (siteManager.isStop()) {
                    return new Response(Boolean.FALSE, "Индексация сайта уже остановлена", HttpStatus.BAD_REQUEST);
                } else {
                    siteManager.stopParsing();
                    return new Response(Boolean.TRUE, null, HttpStatus.OK);
                }
            } else {
                return new Response(Boolean.FALSE, "Данный сайт отсутствует в системе", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new Response(Boolean.FALSE, "Непредвиденная ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void startParsing(List<SiteManager> siteManagers) {
        for (SiteManager siteManager : siteManagers) {
            if (siteManager.isStop()) {
                StartParsingWorker worker = new StartParsingWorker(siteManager);
                Thread thread = new Thread(worker);
                thread.start();
            }
        }
    }
}
