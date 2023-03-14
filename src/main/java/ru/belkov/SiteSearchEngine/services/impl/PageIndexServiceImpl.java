package ru.belkov.SiteSearchEngine.services.impl;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.belkov.SiteSearchEngine.dto.Response;
import ru.belkov.SiteSearchEngine.model.entity.*;
import ru.belkov.SiteSearchEngine.services.*;
import ru.belkov.SiteSearchEngine.util.lemasUtil.LemmasLanguageEnglish;
import ru.belkov.SiteSearchEngine.util.lemasUtil.LemmasLanguageRussian;
import ru.belkov.SiteSearchEngine.util.lemasUtil.LemmasUtil;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

@Service
public class PageIndexServiceImpl implements PageIndexService {
    private final PageService pageService;

    private final IndexService indexService;

    private final LemmaService lemmaService;

    private final SiteService siteService;

    private final FieldService fieldService;

    private final ConnectionService connectionService;

    private static final Logger logger = LoggerFactory.getLogger(PageIndexServiceImpl.class);

    public PageIndexServiceImpl(PageService pageService, IndexService indexService, LemmaService lemmaService, SiteService siteService, FieldService fieldService, ConnectionService connectionService) {
        this.pageService = pageService;
        this.indexService = indexService;
        this.lemmaService = lemmaService;
        this.siteService = siteService;
        this.fieldService = fieldService;
        this.connectionService = connectionService;
    }

    @Override
    public Response indexPage(String url) {
        try {
            Site site = findSite(url);
            if (site == null) {
                return new Response(Boolean.FALSE, "Страница находится за пределами индексируемых сайтов", HttpStatus.NOT_FOUND);
            }
            String relativePath = convertPathToRelative(site.getUrl(), url);
            if (relativePath != null) {
                Page page = pageService.getByUrl(relativePath);
                if (page == null) {
                    page = new Page();
                    page.setPath(relativePath);
                    page.setContent("");
                    page.setCode(0);
                    page.setSite(site);
                    if (pageService.addIfNotExists(page)) {
                        site.setStatusTime(new Timestamp(System.currentTimeMillis()));
                        siteService.updateSiteByUrl(site);
                        if (addPageToIndex(page) != null) {
                            return new Response(Boolean.TRUE, null, HttpStatus.OK);
                        }
                    }
                } else {
                    clearPageData(page);
                    if (addPageToIndex(page) != null) {
                        return new Response(Boolean.TRUE, null, HttpStatus.OK);
                    }
                }
            } else {
                logger.error("Ошибка при формировании относительного пути (site url: " + site.getUrl() + "page url: " + url + ")");
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
            return new Response(Boolean.FALSE, "Непредвиденная ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new Response(Boolean.FALSE, "Непредвиденная ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public Document indexPage(String url, Site site) throws IOException {
        Page page = new Page();
        String relativePath = convertPathToRelative(site.getUrl(), url);
        if (relativePath != null) {
            page.setPath(relativePath);
            page.setContent("");
            page.setCode(0);
            page.setSite(site);
            if (pageService.addIfNotExists(page)) {
                site.setStatusTime(new Timestamp(System.currentTimeMillis()));
                siteService.updateSiteByUrl(site);
                return addPageToIndex(page);
            }
        } else {
            logger.error("Ошибка при формировании относительного пути (site url: " + site.getUrl() + "page url: " + url + ")");
        }
        return null;
    }

    private Document addPageToIndex(Page page) throws IOException {
        String url = page.getSite().getUrl() + page.getPath();
        Connection.Response response = connectionService.getResponse(page.getSite(), url);
        String contentType = response.contentType();
        if (contentType != null && contentType.contains("text/") && (response.statusCode() != 404 || response.statusCode() != 500)) {
            Document doc = response.parse();
            page.setContent(doc.html());
            page.setCode(response.statusCode());
            page = pageService.updateByPathAndSite(page);
            createLemmasAndIndices(doc, page);
            return doc;
        } else {
            page.setCode(response.statusCode());
            pageService.updateByPathAndSite(page);
        }
        return null;
    }

    private String convertPathToRelative(String siteName, String absolutePath) {
        String siteUrlWithHttp;
        String siteUrlWithHttps;
        if (siteName.startsWith("http")) {
            siteUrlWithHttp = siteName;
            siteUrlWithHttps = siteName.replaceFirst("http", "https");
        } else if (siteName.startsWith("https")) {
            siteUrlWithHttp = siteName.replaceFirst("https", "http");
            siteUrlWithHttps = siteName;
        } else {
            return null;
        }

        if (absolutePath.equals(siteUrlWithHttp) || absolutePath.equals(siteUrlWithHttps)) {
            return "/";
        } else if (absolutePath.startsWith(siteUrlWithHttp)) {
            return absolutePath.substring(siteUrlWithHttp.length());
        } else if (absolutePath.startsWith(siteUrlWithHttps)) {
            return absolutePath.substring(siteUrlWithHttps.length());
        } else {
            return null;
        }
    }

    private void createLemmasAndIndices(Document doc, Page page) throws IOException {
        Set<Lemma> lemmaSet = new HashSet<>();
        for (Field field : fieldService.getAll()) {
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

    private synchronized void clearPageData(Page page) {
        List<Index> indices = indexService.getIndexByPage(page);
        for (Index index : indices) {
            Lemma lemma = index.getLemma();
            lemmaService.decrementFrequency(lemma);
            indexService.deleteIndex(index);
        }
    }

    private Site findSite(String url) {
        List<Site> sites = siteService.getAll();
        for (Site site : sites) {
            if (url.startsWith(site.getUrl())) {
                return site;
            }
        }
        return null;
    }
}
