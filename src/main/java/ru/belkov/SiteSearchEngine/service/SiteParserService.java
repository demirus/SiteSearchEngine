package ru.belkov.SiteSearchEngine.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.belkov.SiteSearchEngine.model.entity.Site;
import ru.belkov.SiteSearchEngine.config.SiteParserConfig;
import ru.belkov.SiteSearchEngine.model.SiteParser;
import java.util.List;
@Service
public class SiteParserService {
    private final SiteParserConfig siteParserConfig;

    private final IndexService indexService;

    private final LemmaService lemmaService;

    private final PageService pageService;

    private final SiteService siteService;

    private final Logger logger = LoggerFactory.getLogger(SiteParserService.class);

    @Autowired
    public SiteParserService(SiteParserConfig siteParserConfig, IndexService indexService, LemmaService lemmaService, PageService pageService, SiteService siteService) {
        this.siteParserConfig = siteParserConfig;
        this.indexService = indexService;
        this.lemmaService = lemmaService;
        this.pageService = pageService;
        this.siteService = siteService;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(ContextRefreshedEvent event) {
        event.getApplicationContext().getBean(SiteParserService.class).startParsing();
    }

    public void startParsing() {
        List<Site> sites = siteParserConfig.getSites();
        logger.info("sites parse starts...");
        for (Site site : sites) {
            SiteParser siteParser = new SiteParser(site, site.getUrl(), siteParserConfig, pageService, lemmaService, indexService, siteService);
            siteParser.fork();
        }
    }
}
