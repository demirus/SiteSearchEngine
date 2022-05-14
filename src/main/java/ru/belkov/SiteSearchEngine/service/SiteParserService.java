package ru.belkov.SiteSearchEngine.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.belkov.SiteSearchEngine.model.Site;
import ru.belkov.SiteSearchEngine.config.SiteParserConfig;
import ru.belkov.SiteSearchEngine.model.SiteParser;
import ru.belkov.SiteSearchEngine.repository.PageRepository;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

@Component
public class SiteParserService {
    @Autowired
    private SiteParserConfig siteParserConfig;

    @Autowired
    private PageRepository pageRepository;

    private Logger logger = LoggerFactory.getLogger(SiteParserService.class);

    @PostConstruct
    public void startParsing() {
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        List<Site> sites = siteParserConfig.getSites();
        logger.info("sites parse starts...");
        for (Site site : sites){
            forkJoinPool.invoke(new SiteParser(site, pageRepository, siteParserConfig));
        }
        logger.info("sites parse complete...");
    }
}
