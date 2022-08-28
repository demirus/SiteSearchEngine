package ru.belkov.SiteSearchEngine.service;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import ru.belkov.SiteSearchEngine.config.SiteParserConfig;
import ru.belkov.SiteSearchEngine.model.entity.*;
import ru.belkov.SiteSearchEngine.util.lemasUtil.LemmasLanguageEnglish;
import ru.belkov.SiteSearchEngine.util.lemasUtil.LemmasLanguageRussian;
import ru.belkov.SiteSearchEngine.util.lemasUtil.LemmasUtil;

import java.io.IOException;
import java.util.*;

@Service
public class PageIndexServiceImpl implements PageIndexService {
    private final SiteParserConfig siteParserConfig;

    private final PageService pageService;

    private final IndexService indexService;

    private final LemmaService lemmaService;

    public PageIndexServiceImpl(SiteParserConfig siteParserConfig, PageService pageService, IndexService indexService, LemmaService lemmaService) {
        this.siteParserConfig = siteParserConfig;
        this.pageService = pageService;
        this.indexService = indexService;
        this.lemmaService = lemmaService;
    }

    @Override
    public boolean indexPage(String url) {
        try {
            Page page = pageService.getByUrl(url);
            if (page == null) {
                Site site = findSite(url);
                if (site == null) {
                    return false;
                }
            } else {
                clearPageData(page);
                addPageToIndex(page);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void addPageToIndex(Page page) throws IOException {
        String url = page.getPath();
        Connection.Response response = createResponse(url);
        String contentType = response.contentType();
        if (contentType != null && contentType.contains("text/") && (response.statusCode() != 404 || response.statusCode() != 500)) {
            Document doc = response.parse();
            page.setContent(doc.html());
            page.setCode(response.statusCode());
            page = pageService.updateByPathAndSite(page);
            createLemmasAndIndices(doc, page);
        } else {
            page.setCode(response.statusCode());
            pageService.updateByPathAndSite(page);
        }
    }

    private Connection.Response createResponse(String url) throws IOException {
        return Jsoup.connect(url)
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .userAgent(siteParserConfig.getUserAgent())
                .referrer(siteParserConfig.getReferrer())
                .execute();
    }

    private void createLemmasAndIndices(Document doc, Page page) throws IOException {
        Set<Lemma> lemmaSet = new HashSet<>();
        for (Field field : siteParserConfig.getFields()) {
            Elements elements = doc.select(field.getSelector());
            Map<String, Integer> lemmas = LemmasUtil.getLemmas(elements.text(), Arrays.asList(new LemmasLanguageRussian(), new LemmasLanguageEnglish()));
            for (Map.Entry<String, Integer> entry : lemmas.entrySet()) {
                Lemma lemma = new Lemma();
                lemma.setLemma(entry.getKey());
                lemma.setFrequency(0);
                lemma.setSite(page.getSite());
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

    private void clearPageData(Page page) {
        List<Index> indices = indexService.getIndexByPage(page);
        for (Index index : indices) {
            Lemma lemma = index.getLemma();
            lemmaService.decrementFrequency(lemma);
            indexService.deleteIndex(index);
        }
    }

    private Site findSite(String url) {
        List<Site> sites = siteParserConfig.getSites();
        for (Site site : sites) {
            if (url.startsWith(site.getUrl())) {
                return site;
            }
        }
        return null;
    }
}
