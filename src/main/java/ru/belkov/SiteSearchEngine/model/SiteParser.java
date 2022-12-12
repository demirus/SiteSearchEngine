package ru.belkov.SiteSearchEngine.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.belkov.SiteSearchEngine.model.entity.*;
import org.jsoup.nodes.Document;
import ru.belkov.SiteSearchEngine.services.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.stream.Collectors;


public class SiteParser extends RecursiveAction {
    private static final Logger logger = LoggerFactory.getLogger(SiteParser.class);
    private final Site site;
    private String url;
    private PageIndexService pageIndexService;

    private SiteManager manager;

    public SiteParser(Site site, String url, SiteManager manager, PageIndexService pageIndexService) {
        this.site = site;
        this.url = url;
        this.manager = manager;
        this.pageIndexService = pageIndexService;
    }

    @Override
    protected void compute() {
        try {
            if (!manager.isStop()) {
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
            dividedTasks.add(new SiteParser(site, link, manager, pageIndexService));
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
}
