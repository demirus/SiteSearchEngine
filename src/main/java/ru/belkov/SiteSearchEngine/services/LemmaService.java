package ru.belkov.SiteSearchEngine.services;

import ru.belkov.SiteSearchEngine.model.entity.Lemma;
import ru.belkov.SiteSearchEngine.model.entity.Site;

import java.util.List;

public interface LemmaService {
    Lemma addIfNotExists(Lemma lemma);

    void incrementFrequency(Lemma lemma);

    List<Lemma> getLemmasByLemma(String lemma);

    long count();

    void decrementFrequency(Lemma lemma);

    Lemma getLemmaByLemmaAndSite(String lemma, Site site);
}
