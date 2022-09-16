package ru.belkov.SiteSearchEngine.service;

import ru.belkov.SiteSearchEngine.model.entity.Lemma;

import java.util.List;

public interface LemmaService {
    Lemma addIfNotExists(Lemma lemma);

    void incrementFrequency(Lemma lemma);

    List<Lemma> getLemmasByLemma(String lemma);

    long count();

    void decrementFrequency(Lemma lemma);
}
