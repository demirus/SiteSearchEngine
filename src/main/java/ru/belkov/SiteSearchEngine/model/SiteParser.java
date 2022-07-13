package ru.belkov.SiteSearchEngine.model;

import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.belkov.SiteSearchEngine.config.SiteParserConfig;
import ru.belkov.SiteSearchEngine.enums.SiteStatus;
import ru.belkov.SiteSearchEngine.model.entity.*;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import ru.belkov.SiteSearchEngine.service.IndexService;
import ru.belkov.SiteSearchEngine.service.LemmaService;
import ru.belkov.SiteSearchEngine.service.PageService;
import ru.belkov.SiteSearchEngine.service.SiteService;
import ru.belkov.SiteSearchEngine.util.lemasUtil.LemmasLanguageEnglish;
import ru.belkov.SiteSearchEngine.util.lemasUtil.LemmasLanguageRussian;
import ru.belkov.SiteSearchEngine.util.lemasUtil.LemmasUtil;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


public class SiteParser extends RecursiveAction {

    private static final Logger logger = LoggerFactory.getLogger(SiteParser.class);

    private static AtomicInteger siteParsersCount = new AtomicInteger(0);

    private final PageService pageService;

    private final SiteParserConfig config;

    private final LemmaService lemmaService;

    private final IndexService indexService;

    private final SiteService siteService;

    private final Site site;

    private String url;

    public SiteParser(Site site, String url, SiteParserConfig config, PageService pageService, LemmaService lemmaService, IndexService indexService, SiteService siteService) {
        this.site = site;
        this.pageService = pageService;
        this.config = config;
        this.lemmaService = lemmaService;
        this.indexService = indexService;
        this.url = url;
        this.siteService = siteService;
    }



    @Override
    protected void compute() {
        try {
            siteParsersCount.incrementAndGet();
            Page page = new Page();
            page.setPath(url);
            page.setContent("");
            page.setCode(0);
            page.setSite(site);
            if (pageService.addIfNotExists(page)) {
                site.setStatusTime(new Timestamp(System.currentTimeMillis()));
                site.setStatus(SiteStatus.INDEXING);
                siteService.updateSiteByUrl(site);
                Connection.Response response = createResponse(url);
                String contentType = response.contentType();
                if (contentType != null && contentType.contains("text/") && (response.statusCode() != 404 || response.statusCode() != 500)) {
                    Document doc = response.parse();
                    page.setContent(doc.html());
                    page.setCode(response.statusCode());
                    page = pageService.updateByPathAndSite(page);
                    createLemmasAndIndices(doc, page);
                    Set<String> links = getLinks(doc);
                    if (links.size() > 0) {
                        ForkJoinTask.invokeAll(createSubtasks(links));
                    }
                } else {
                    page.setCode(response.statusCode());
                    pageService.updateByPathAndSite(page);
                }
            }
            siteParsersCount.decrementAndGet();
            if (siteParsersCount.decrementAndGet() == 0) {
                site.setStatus(SiteStatus.INDEXED);
                siteService.updateSiteByUrl(site);
            }
        } catch (IOException e) {
            site.setLastError(e.getMessage());
            site.setStatus(SiteStatus.FAILED);
            siteService.updateSiteByUrl(site);
            logger.error(e + " URL: " + site.getUrl());
        }
    }

    private Set<String> getLinks(Document doc) {
        return doc.select("a[href]")
                .stream()
                .map(l -> l.attr("abs:href"))
                .filter(this::isSameDomain)
                .filter(SiteParser::isNotHashMark)
                .collect(Collectors.toSet());
    }

    private Connection.Response createResponse(String url) throws IOException {
        return Jsoup.connect(url)
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .userAgent(config.getUserAgent())
                .referrer(config.getReferrer())
                .execute();
    }

    private void createLemmasAndIndices(Document doc, Page page) throws IOException {
        Set<Lemma> lemmaSet = new HashSet<>();
        for (Field field : config.getFields()) {
            Elements elements = doc.select(field.getSelector());
            Map<String, Integer> lemmas = LemmasUtil.getLemmas(elements.text(), Arrays.asList(new LemmasLanguageRussian(), new LemmasLanguageEnglish()));
            for (Map.Entry<String, Integer> entry : lemmas.entrySet()) {
                Lemma lemma = new Lemma();
                lemma.setLemma(entry.getKey());
                lemma.setFrequency(0);
                lemma.setSite(site);
                lemma = lemmaService.addIfNotExists(lemma);
                lemmaSet.add(lemma);
                Index index = indexService.findIndexByLemmaAndPage(lemma, page);
                if (index != null) {
                    index.setRank(index.getRank() + calculateFieldRank(field, entry.getValue()));
                    indexService.save(index);
                } else {
                    index = new Index();
                    index.setPage(page);
                    index.setLemma(lemma);
                    index.setRank(calculateFieldRank(field, entry.getValue()));
                    indexService.save(index);
                }
            }
        }
        lemmaSet.forEach(lemmaService::incrementFrequency);
    }

    private double calculateFieldRank(Field field, Integer count) {
        return field.getWeight() * count;
    }


    private List<SiteParser> createSubtasks(Set<String> links) {
        List<SiteParser> dividedTasks = new ArrayList<>();
        for (String link : links) {
            dividedTasks.add(new SiteParser(site, link, config, pageService, lemmaService, indexService, siteService));
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
