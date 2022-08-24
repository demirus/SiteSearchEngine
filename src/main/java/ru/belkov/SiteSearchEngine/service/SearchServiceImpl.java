package ru.belkov.SiteSearchEngine.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.belkov.SiteSearchEngine.config.SearchServiceConfig;
import ru.belkov.SiteSearchEngine.config.SiteParserConfig;
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

    private final SiteParserConfig parserConfig;

    private final SearchServiceConfig searchConfig;

    public SearchServiceImpl(LemmaService lemmaService, IndexService indexService, SiteParserConfig parserConfig, SearchServiceConfig searchConfig) {
        this.lemmaService = lemmaService;
        this.indexService = indexService;
        this.parserConfig = parserConfig;
        this.searchConfig = searchConfig;
    }

    @Override
    public List<SearchPage> search(String searchRequest, Site site) {
//        List<Lemma> lemmas =
        return null;
    }

    @Override
    public List<SearchPage> search(String searchRequest) {
        try {
            List<Lemma> lemmas = getLemmasFromRequest(searchRequest);
            deleteAllLemmasWithTooHighFrequency(lemmas);
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

    private void deleteAllLemmasWithTooHighFrequency(List<Lemma> lemmas) {
        long configMaxFrequency = searchConfig.getMaxLemmaFrequency();
        lemmas.removeIf(l -> l.getFrequency() >= configMaxFrequency);
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

    private List<SearchPage> getPagesWithLemmas(Collection<Index> indexes, List<Lemma> lemmas) throws EntityNotFoundException, IOException {
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

    private List<SearchPage> createSearchPages(Set<Page> pages, List<Lemma> lemmas) throws EntityNotFoundException, IOException {
        List<SearchPage> searchPages = new ArrayList<>();
        double maxAbsoluteRelevance = getMaxAbsoluteRelevance(pages, lemmas);
        for (Page page : pages) {
            SearchPage searchPage = new SearchPage();
            searchPage.uri = page.getPath();
            searchPage.title = page.getTitle();
            searchPage.relevance = getAbsoluteRelevance(page, lemmas) / maxAbsoluteRelevance;
            searchPage.snippet = getSnippet(page, lemmas);
            searchPages.add(searchPage);
        }
        searchPages.sort(Comparator.comparingDouble(sp -> -sp.relevance));
        return searchPages;
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

    private String getSnippet(Page page, List<Lemma> lemmas) throws IOException {
        Map<String, String> fragments = new HashMap<>();
        Document doc = Jsoup.parse(page.getContent());
        for (Field field : parserConfig.getFields()) {
            Elements elements = doc.select(field.getSelector());
            Map<String, String> docLemmas = LemmasUtil.getLemmasWithOriginalWords(elements.text(), Arrays.asList(new LemmasLanguageRussian(), new LemmasLanguageEnglish()));
            for (Lemma lemma : lemmas) {
                if (docLemmas.containsKey(lemma.getLemma())) {
                    String originalWord = docLemmas.get(lemma.getLemma());
                    String fragment = getHtmlFragmentWithWord(originalWord, page);
                    String clearedFromBTag = fragment.replaceAll("<b>", "").replaceAll("</b>", "");
                    if (!fragments.containsKey(clearedFromBTag)) {
                        fragments.put(clearedFromBTag, fragment);
                    } else {
                        String textBetweenTag = fragment.substring(fragment.indexOf("<b>") + 3, fragment.indexOf("</b>"));
                        fragments.replace(clearedFromBTag, fragments.get(clearedFromBTag).replaceAll(textBetweenTag, "<b>" + textBetweenTag + "</b>"));
                    }
                }
            }

        }
        return String.join("\n", fragments.values());
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
