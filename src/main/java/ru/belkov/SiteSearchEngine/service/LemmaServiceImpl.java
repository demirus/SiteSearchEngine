package ru.belkov.SiteSearchEngine.service;

import org.springframework.stereotype.Service;
import ru.belkov.SiteSearchEngine.model.entity.Lemma;
import ru.belkov.SiteSearchEngine.repository.LemmaRepository;

@Service
public class LemmaServiceImpl implements LemmaService {
    LemmaRepository repository;

    public LemmaServiceImpl(LemmaRepository repository) {
        this.repository = repository;
    }

    @Override
    public synchronized Lemma addIfNotExists(Lemma lemma) {
        if (!repository.existsLemmaByLemma(lemma.getLemma())) {
            return repository.saveAndFlush(lemma);
        }
        return repository.findLemmaByLemma(lemma.getLemma());
    }

    @Override
    public synchronized void incrementFrequency(Lemma lemma) {
        Lemma lemmaDB = repository.findLemmaByLemma(lemma.getLemma());
        lemmaDB.setFrequency(lemmaDB.getFrequency() + 1);
        repository.save(lemmaDB);
    }

    @Override
    public synchronized Lemma getLemmaByLemma(String lemma) {
        return repository.findLemmaByLemma(lemma);
    }
}
