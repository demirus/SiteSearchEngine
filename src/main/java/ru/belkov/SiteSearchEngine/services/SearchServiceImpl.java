package ru.belkov.SiteSearchEngine.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.belkov.SiteSearchEngine.config.SearchServiceConfig;
import ru.belkov.SiteSearchEngine.config.SiteParserConfig;
import ru.belkov.SiteSearchEngine.dto.search.SearchDataObject;
import ru.belkov.SiteSearchEngine.dto.search.SearchResponse;
import ru.belkov.SiteSearchEngine.dto.search.SearchResponseError;
import ru.belkov.SiteSearchEngine.dto.search.SearchResponseSuccess;
import ru.belkov.SiteSearchEngine.model.entity.*;
import ru.belkov.SiteSearchEngine.util.lemasUtil.LemmasLanguageEnglish;
import ru.belkov.SiteSearchEngine.util.lemasUtil.LemmasLanguageRussian;
import ru.belkov.SiteSearchEngine.util.lemasUtil.LemmasUtil;

import ru.belkov.SiteSearchEngine.exceptions.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {
    private final Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);

    private final LemmaService lemmaService;

    private final IndexService indexService;

    private final FieldService fieldService;

    private final SearchServiceConfig searchConfig;

    private final SiteService siteService;

    public SearchServiceImpl(LemmaService lemmaService, IndexService indexService, SiteParserConfig parserConfig, FieldService fieldService, SearchServiceConfig searchConfig, SiteService siteService) {
        this.lemmaService = lemmaService;
        this.indexService = indexService;
        this.fieldService = fieldService;
        this.searchConfig = searchConfig;
        this.siteService = siteService;
    }

    @Override
    public SearchResponse search(String searchRequest, Site site) {
        if (searchRequest.isEmpty()) {
            return new SearchResponseError(false, "Задан пустой поисковый запрос", HttpStatus.OK);
        }
        try {
            Map<Site, List<Lemma>> siteLemmasMap = new HashMap<>();
            List<Lemma> lemmas = getLemmasFromRequest(searchRequest, site);
            if (!lemmas.isEmpty()) {
                siteLemmasMap.put(site, lemmas);
            } else {
                return new SearchResponseError(false, "Указанная страница не найдена", HttpStatus.OK);
            }
            return getSearchResponse(siteLemmasMap, searchRequest);
        } catch (IOException | EntityNotFoundException e) {
            logger.error(e.toString());
        }
        return new SearchResponseError(false, "Внутренняя ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public SearchResponse search(String searchRequest) {
        if (searchRequest.isEmpty()) {
            return new SearchResponseError(false, "Задан пустой поисковый запрос", HttpStatus.OK);
        }
        try {
            List<Site> sites = siteService.getAll();
            Map<Site, List<Lemma>> siteLemmasMap = new HashMap<>();
            for (Site site : sites) {
                List<Lemma> lemmas = getLemmasFromRequest(searchRequest, site);
                if (!lemmas.isEmpty()) {
                    siteLemmasMap.put(site, lemmas);
                }
            }
            if (siteLemmasMap.isEmpty()) {
                return new SearchResponseError(false, "Указанная страница не найдена", HttpStatus.OK);
            }
            return getSearchResponse(siteLemmasMap, searchRequest);
        } catch (IOException | EntityNotFoundException e) {
            logger.error(e.toString());
        }
        return new SearchResponseError(false, "Внутренняя ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private SearchResponse getSearchResponse(Map<Site, List<Lemma>> siteLemmasMap, String query) throws EntityNotFoundException, IOException {
        SearchResponseSuccess searchResponse = new SearchResponseSuccess(true, HttpStatus.OK);
        deleteAllLemmasWithTooHighFrequency(siteLemmasMap);
        List<SearchDataObject> searchDataObjects = new ArrayList<>();
        searchResponse.setData(searchDataObjects);
        for (Map.Entry<Site, List<Lemma>> entry : siteLemmasMap.entrySet()) {
            List<Lemma> lemmas = entry.getValue();
            Lemma rarestLemma = lemmas.get(0);
            Collection<Index> indexes = rarestLemma.getIndexes();
            Set<Page> pages = getPagesWithLemmas(indexes, lemmas);
            List<SearchDataObject> objects = createSearchDataObjects(pages, lemmas, query);
            searchResponse.setCount(searchResponse.getCount() + objects.size());
            searchResponse.getData().addAll(objects);
        }
        return searchResponse;
    }

    private void deleteAllLemmasWithTooHighFrequency(Map<Site, List<Lemma>> siteLemmasMap) {
        long configMaxFrequency = searchConfig.getMaxLemmaFrequency();
        for (Map.Entry<Site, List<Lemma>> entry : siteLemmasMap.entrySet()) {
            entry.getValue().removeIf(l -> l.getFrequency() >= configMaxFrequency);
        }
    }

    private List<Lemma> getLemmasFromRequest(String request, Site site) throws IOException {
        Map<String, Integer> stringLemmas = LemmasUtil.getLemmas(request, Arrays.asList(new LemmasLanguageRussian(), new LemmasLanguageEnglish()));
        List<Lemma> lemmas = new ArrayList<>();
        for (String stringLemma : stringLemmas.keySet()) {
            Lemma lemma = lemmaService.getLemmaByLemmaAndSite(stringLemma, site);
            if (lemma != null) {
                lemmas.add(lemma);
            }
        }
        lemmas.sort(Comparator.comparing(Lemma::getFrequency));
        return lemmas;
    }

    private Set<Page> getPagesWithLemmas(Collection<Index> indexes, List<Lemma> lemmas) {
        List<Index> indexList = new ArrayList<>(indexes);
        indexList.removeIf(index -> {
            Page page = index.getPage();
            for (Lemma lemma : lemmas) {
                if (indexService.findIndexByLemmaAndPage(lemma, page) == null) {
                    return true;
                }
            }
            return false;
        });
        return indexList.stream().map(Index::getPage).collect(Collectors.toSet());
    }

    private List<SearchDataObject> createSearchDataObjects(Set<Page> pages, List<Lemma> lemmas, String query) throws EntityNotFoundException, IOException {
        List<SearchDataObject> searchDataObjects = new ArrayList<>();
        double maxAbsoluteRelevance = getMaxAbsoluteRelevance(pages, lemmas);
        for (Page page : pages) {
            String snippet = getSnippet(page, query);
            if (!snippet.equals("")) {
                SearchDataObject searchDataObject = new SearchDataObject();
                searchDataObject.setSite(page.getSite().getUrl());
                searchDataObject.setSiteName(page.getSite().getName());
                searchDataObject.setUri(page.getPath());
                searchDataObject.setTitle(page.getTitle());
                searchDataObject.setSnippet(snippet);
                searchDataObject.setRelevance(getAbsoluteRelevance(page, lemmas) / maxAbsoluteRelevance);
                searchDataObjects.add(searchDataObject);
            }
        }
        searchDataObjects.sort(Comparator.comparingDouble(sp -> -sp.getRelevance()));
        return searchDataObjects;
    }

    private double getMaxAbsoluteRelevance(Set<Page> pages, List<Lemma> lemmas) throws EntityNotFoundException {
        double maxRelevance = 0.0;
        for (Page page : pages) {
            double relevance = getAbsoluteRelevance(page, lemmas);
            if (maxRelevance < relevance) {
                maxRelevance = relevance;
            }
        }
        return maxRelevance;
    }

    private double getAbsoluteRelevance(Page page, List<Lemma> lemmas) throws EntityNotFoundException {
        double relevance = 0.0;
        for (Lemma lemma : lemmas) {
            Index index = indexService.findIndexByLemmaAndPage(lemma, page);
            if (index == null) {
                throw new EntityNotFoundException("Entity Index not found by page: " + page + " and lemma: " + lemma);
            }
            relevance += index.getRank();
        }
        return relevance;
    }

    private String getSnippet(Page page, String query) {
        return getHtmlFragmentWithWord(query, page);
    }

    private String getHtmlFragmentWithWord(String word, Page page) {
        Document document = Jsoup.parse(page.getContent());
        Elements elements = document.select(":contains(%)".replace("%", word));
        String fragment = getSmallestTextFragment(elements);
        if (fragment != null) {
            return "<p>" + (fragment.replaceAll(word, "<b>" + word + "</b>")) + "</p>";
        } else {
            return "";
        }
    }

    private String getSmallestTextFragment(Elements elements) {
        Comparator<String> byLength = (e1, e2) -> Integer.compare(e2.length(), e1.length());
        Optional<String> fragment = elements.stream().map(Element::text).sorted(byLength.reversed()).findFirst();
        return fragment.orElse(null);
    }

}
