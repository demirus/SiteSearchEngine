package ru.belkov.SiteSearchEngine.model;

import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.belkov.SiteSearchEngine.config.SiteParserConfig;
import ru.belkov.SiteSearchEngine.model.entity.Field;
import ru.belkov.SiteSearchEngine.model.entity.Index;
import ru.belkov.SiteSearchEngine.model.entity.Lemma;
import ru.belkov.SiteSearchEngine.model.entity.Page;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import ru.belkov.SiteSearchEngine.service.IndexService;
import ru.belkov.SiteSearchEngine.service.LemmaService;
import ru.belkov.SiteSearchEngine.service.PageService;
import ru.belkov.SiteSearchEngine.util.lemasUtil.LemmasLanguageEnglish;
import ru.belkov.SiteSearchEngine.util.lemasUtil.LemmasLanguageRussian;
import ru.belkov.SiteSearchEngine.util.lemasUtil.LemmasUtil;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.stream.Collectors;


public class SiteParser extends RecursiveAction {

    private static final Logger logger = LoggerFactory.getLogger(SiteParser.class);

    private final PageService pageService;

    private final SiteParserConfig config;

    private final LemmaService lemmaService;

    private final IndexService indexService;

    private final Site site;

    public SiteParser(Site site, SiteParserConfig config, PageService pageService, LemmaService lemmaService, IndexService indexService) {
        this.site = site;
        this.pageService = pageService;
        this.config = config;
        this.lemmaService = lemmaService;
        this.indexService = indexService;
    }

    @Override
    protected void compute() {
        try {
            Page page = new Page();
            page.setPath(site.getUrl());
            page.setContent("");
            page.setCode(0);
            if (pageService.addIfNotExists(page)) {
                Connection.Response response = createResponse(site.getUrl());
                String contentType = response.contentType();
                if (contentType != null && contentType.contains("text/") && (response.statusCode() != 404 || response.statusCode() != 500)) {
                    Document doc = response.parse();
                    page.setContent(doc.html());
                    page.setCode(response.statusCode());
                    page = pageService.updateByPath(page);
                    createLemmasAndIndices(doc, page);
                    Set<String> links = getLinks(doc);
                    if (links.size() > 0) {
                        ForkJoinTask.invokeAll(createSubtasks(links));
                    }
                } else {
                    page.setCode(response.statusCode());
                    pageService.updateByPath(page);
                }
            }
        } catch (IOException e) {
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
        return Jsoup.connect(site.getUrl())
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
            Site site = new Site();
            site.setUrl(link);
            dividedTasks.add(new SiteParser(site, config, pageService, lemmaService, indexService));
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
