package ru.belkov.SiteSearchEngine.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.belkov.SiteSearchEngine.config.SiteParserConfig;
import ru.belkov.SiteSearchEngine.model.entity.*;
import org.jsoup.nodes.Document;
import ru.belkov.SiteSearchEngine.service.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.stream.Collectors;


public class SiteParser extends RecursiveAction {

    private static final Logger logger = LoggerFactory.getLogger(SiteParser.class);

    private static boolean stop;

    private final PageService pageService;

    private final SiteParserConfig config;

    private final LemmaService lemmaService;

    private final IndexService indexService;

    private final SiteService siteService;

    private final Site site;

    private String url;

    private PageIndexService pageIndexService;

    public SiteParser(Site site, String url, SiteParserConfig config, PageService pageService, LemmaService lemmaService, IndexService indexService, SiteService siteService, PageIndexService pageIndexService) {
        this.site = site;
        this.pageService = pageService;
        this.config = config;
        this.lemmaService = lemmaService;
        this.indexService = indexService;
        this.url = url;
        this.siteService = siteService;
        this.pageIndexService = pageIndexService;
    }

    @Override
    protected void compute() {
        try {
            if (!stop) {
                Document doc = pageIndexService.indexPage(url, site);
                if (doc != null) {
                    Set<String> links = getLinks(doc);
                    if (links.size() > 0) {
                        ForkJoinTask.invokeAll(createSubtasks(links));
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    private Set<String> getLinks(Document doc) {
        return doc.select("a[href]").stream().map(l -> l.attr("abs:href")).filter(this::isSameDomain).filter(SiteParser::isNotHashMark).collect(Collectors.toSet());
    }

    private List<SiteParser> createSubtasks(Set<String> links) {
        List<SiteParser> dividedTasks = new ArrayList<>();
        for (String link : links) {
            dividedTasks.add(new SiteParser(site, link, config, pageService, lemmaService, indexService, siteService, pageIndexService));
        }
        return dividedTasks;
    }

    private static boolean isNotHashMark(String root) {
        return !root.contains("#");
    }

    private boolean isSameDomain(String url) {
        URI uri1 = null;
        URI uri2 = null;
        try {
            uri1 = new URI(url);
            uri2 = new URI(site.getUrl());
        } catch (URISyntaxException ignored) {

        }
        if (uri1 != null && uri2 != null) {
            String domain1 = uri1.getHost();
            String domain2 = uri2.getHost();
            if (domain1 != null && domain2 != null) {
                return domain1.equals(domain2);
            }
        }
        return false;
    }

    public static void stopIndexing() {
        stop = true;
    }

    public static void startIndexing() {
        stop = false;
    }
}
