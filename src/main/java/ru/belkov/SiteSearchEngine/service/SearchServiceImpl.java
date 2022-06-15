package ru.belkov.SiteSearchEngine.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.belkov.SiteSearchEngine.model.entity.Index;
import ru.belkov.SiteSearchEngine.model.entity.Lemma;
import ru.belkov.SiteSearchEngine.model.entity.Page;
import ru.belkov.SiteSearchEngine.model.entity.SearchPage;
import ru.belkov.SiteSearchEngine.util.lemasUtil.LemmasLanguageEnglish;
import ru.belkov.SiteSearchEngine.util.lemasUtil.LemmasLanguageRussian;
import ru.belkov.SiteSearchEngine.util.lemasUtil.LemmasUtil;

import ru.belkov.SiteSearchEngine.exceptions.*;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {
    private Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);

    private final LemmaService lemmaService;

    private final IndexService indexService;

    public SearchServiceImpl(LemmaService lemmaService, IndexService indexService) {
        this.lemmaService = lemmaService;
        this.indexService = indexService;
    }

    @Override
    public List<SearchPage> search(String searchRequest) {
        try {
            List<Lemma> lemmas = getLemmasFromRequest(searchRequest);
            if (lemmas.isEmpty()) {
                return new ArrayList<>();
            }
            Lemma rarestLemma = lemmas.get(0);
            Collection<Index> indexes = rarestLemma.getIndexes();
            return getPagesWithLemmas(indexes, lemmas);
        } catch (IOException | EntityNotFoundException e) {
            logger.error(e.toString());
        }
        return null;
    }

    private List<Lemma> getLemmasFromRequest(String request) throws IOException {
        Map<String, Integer> stringLemmas = LemmasUtil.getLemmas(request, Arrays.asList(new LemmasLanguageRussian(), new LemmasLanguageEnglish()));
        List<Lemma> lemmas = new ArrayList<>();
        for (String stringLemma : stringLemmas.keySet()) {
            Lemma lemma = lemmaService.getLemmaByLemma(stringLemma);
            if (lemma != null) {
                lemmas.add(lemma);
            }
        }
        lemmas.sort(Comparator.comparing(Lemma::getFrequency));
        return lemmas;
    }

    private List<SearchPage> getPagesWithLemmas(Collection<Index> indexes, List<Lemma> lemmas) throws EntityNotFoundException {
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
        Set<Page> pages = indexList.stream().map(Index::getPage).collect(Collectors.toSet());
        return createSearchPages(pages, lemmas);
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

    private List<SearchPage> createSearchPages(Set<Page> pages, List<Lemma> lemmas) throws EntityNotFoundException {
        List<SearchPage> searchPages = new ArrayList<>();
        double maxAbsoluteRelevance = getMaxAbsoluteRelevance(pages, lemmas);
        for (Page page : pages) {
            SearchPage searchPage = new SearchPage();
            searchPage.uri = page.getPath();
            searchPage.title = page.getTitle();
            searchPage.relevance = getAbsoluteRelevance(page, lemmas) / maxAbsoluteRelevance;
            searchPages.add(searchPage);
        }
        searchPages.sort(Comparator.comparingDouble(sp -> -sp.relevance));
        return searchPages;
    }

}
