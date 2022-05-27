package ru.belkov.SiteSearchEngine.model;

import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.belkov.SiteSearchEngine.config.SiteParserConfig;
import ru.belkov.SiteSearchEngine.model.entity.Page;
import ru.belkov.SiteSearchEngine.repository.PageRepository;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

import java.util.stream.Collectors;


public class SiteParser extends RecursiveAction {

    private static final Set<String> checkedLinksSet = Collections.synchronizedSet(new HashSet<>());

    private static Logger logger = LoggerFactory.getLogger(SiteParser.class);

    private final PageRepository pageRepository;

    private final SiteParserConfig config;

    private final Site site;

    public SiteParser(Site site, PageRepository pageRepository, SiteParserConfig config) {
        this.site = site;
        this.pageRepository = pageRepository;
        this.config = config;
    }

    @Override
    protected void compute() {
        try {
            if (!checkedLinksSet.contains(site.getUrl())) {
                checkedLinksSet.add(site.getUrl());
                Connection.Response response = Jsoup.connect(site.getUrl())
                        .ignoreContentType(true)
                        .ignoreHttpErrors(true)
                        .userAgent(config.getUserAgent())
                        .referrer(config.getReferrer())
                        .execute();
                String contentType = response.contentType();
                if (contentType != null && contentType.contains("text/html")) {
                    Document doc = response.parse();
                    Page page = new Page();
                    page.setPath(site.getUrl());
                    page.setContent(doc.html());
                    page.setCode(response.statusCode());
                    pageRepository.save(page);
                    Set<String> links;
                    links = doc.select("a[href]")
                            .stream()
                            .map(l -> l.attr("abs:href"))
                            .filter(this::isSameDomain)
                            .filter(SiteParser::isNotHashMark)
                            .collect(Collectors.toSet());
                    if (links.size() > 0) {
                        ForkJoinTask.invokeAll(createSubtasks(links));
                    }
                } else {
                    Page page = new Page();
                    page.setPath(site.getUrl());
                    page.setContent("");
                    page.setCode(response.statusCode());
                    pageRepository.save(page);
                }
            }
        } catch (Exception e) {
            logger.error(e + " URL: " + site.getUrl());
        }
    }

    private List<SiteParser> createSubtasks(Set<String> links) {
        List<SiteParser> dividedTasks = new ArrayList<>();
        for (String link : links) {
            Site site = new Site();
            site.setUrl(link);
            dividedTasks.add(new SiteParser(site, pageRepository, config));
        }
        return dividedTasks;
    }

    private static boolean isNotHashMark(String root) {
        int slashPoz = root.lastIndexOf('/');
        if (slashPoz != -1 && (slashPoz + 1) != root.length()) {
            return root.charAt(slashPoz + 1) != '#' && root.charAt(root.length() - 1) != '#';
        }
        return true;
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
