package ru.belkov.SiteSearchEngine.service;

import ru.belkov.SiteSearchEngine.model.entity.Lemma;

public interface LemmaService {
    Lemma addIfNotExists(Lemma lemma);

    void incrementFrequency(Lemma lemma);

    Lemma getLemmaByLemma(String lemma);

    long count();
}
