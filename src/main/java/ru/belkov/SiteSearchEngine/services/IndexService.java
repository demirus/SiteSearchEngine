package ru.belkov.SiteSearchEngine.services;

import ru.belkov.SiteSearchEngine.model.entity.Index;
import ru.belkov.SiteSearchEngine.model.entity.Lemma;
import ru.belkov.SiteSearchEngine.model.entity.Page;

import java.util.List;

public interface IndexService {
    Index save(Index index);
    Index findIndexByLemmaAndPage(Lemma lemma, Page page);
    List<Index> getIndexByPage(Page page);
    void deleteIndex(Index index);
}
