package ru.belkov.SiteSearchEngine.service;

import ru.belkov.SiteSearchEngine.model.entity.Index;
import ru.belkov.SiteSearchEngine.model.entity.Lemma;
import ru.belkov.SiteSearchEngine.model.entity.Page;

public interface IndexService {
    Index save(Index index);
    Index findIndexByLemmaAndPage(Lemma lemma, Page page);
}
