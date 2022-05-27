package ru.belkov.SiteSearchEngine.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.belkov.SiteSearchEngine.model.Site;
import ru.belkov.SiteSearchEngine.config.SiteParserConfig;
import ru.belkov.SiteSearchEngine.model.SiteParser;
import ru.belkov.SiteSearchEngine.repository.FieldRepository;
import ru.belkov.SiteSearchEngine.repository.IndexRepository;
import ru.belkov.SiteSearchEngine.repository.LemmaRepository;
import ru.belkov.SiteSearchEngine.repository.PageRepository;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

@Service
public class SiteParserService {
    private final SiteParserConfig siteParserConfig;

    private final PageRepository pageRepository;

    private final IndexRepository indexRepository;

    private final LemmaRepository lemmaRepository;

    private final FieldRepository fieldRepository;


    private Logger logger = LoggerFactory.getLogger(SiteParserService.class);

    @Autowired
    public SiteParserService(SiteParserConfig siteParserConfig, PageRepository pageRepository, IndexRepository indexRepository, LemmaRepository lemmaRepository, FieldRepository fieldRepository) {
        this.siteParserConfig = siteParserConfig;
        this.pageRepository = pageRepository;
        this.indexRepository = indexRepository;
        this.lemmaRepository = lemmaRepository;
        this.fieldRepository = fieldRepository;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(ContextRefreshedEvent event) {
        event.getApplicationContext().getBean(SiteParserService.class).startParsing();
    }

    public void startParsing() {
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        List<Site> sites = siteParserConfig.getSites();
        logger.info("sites parse starts...");
        for (Site site : sites){
            logger.info("the parsing of site: " + site.getUrl() + " has started...");
            forkJoinPool.invoke(new SiteParser(site, pageRepository, siteParserConfig));
            logger.info("the parsing of site: " + site.getUrl() + " has over...");
        }
        logger.info("sites parse complete...");
    }
}
