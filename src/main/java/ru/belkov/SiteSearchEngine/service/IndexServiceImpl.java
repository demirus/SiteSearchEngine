package ru.belkov.SiteSearchEngine.service;

import org.springframework.stereotype.Service;
import ru.belkov.SiteSearchEngine.model.entity.Index;
import ru.belkov.SiteSearchEngine.model.entity.Lemma;
import ru.belkov.SiteSearchEngine.model.entity.Page;
import ru.belkov.SiteSearchEngine.repository.IndexRepository;

@Service
public class IndexServiceImpl implements IndexService {

    IndexRepository repository;

    public IndexServiceImpl(IndexRepository repository) {
        this.repository = repository;
    }

    @Override
    public Index save(Index index) {
        return repository.saveAndFlush(index);
    }

    @Override
    public Index findIndexByLemmaAndPage(Lemma lemma, Page page) {
        return repository.findIndexByLemmaAndPage(lemma, page);
    }
}
