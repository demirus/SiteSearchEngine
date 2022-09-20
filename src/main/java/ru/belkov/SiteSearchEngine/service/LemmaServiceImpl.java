package ru.belkov.SiteSearchEngine.service;

import org.springframework.stereotype.Service;
import ru.belkov.SiteSearchEngine.model.entity.Lemma;
import ru.belkov.SiteSearchEngine.model.entity.Site;
import ru.belkov.SiteSearchEngine.repository.LemmaRepository;

import java.util.List;

@Service
public class LemmaServiceImpl implements LemmaService {
    LemmaRepository repository;

    public LemmaServiceImpl(LemmaRepository repository) {
        this.repository = repository;
    }

    @Override
    public synchronized Lemma addIfNotExists(Lemma lemma) {
        if (!repository.existsLemmaByLemmaAndSite(lemma.getLemma(), lemma.getSite())) {
            return repository.saveAndFlush(lemma);
        }
        return repository.findLemmaByLemmaAndSite(lemma.getLemma(), lemma.getSite());
    }

    @Override
    public synchronized void incrementFrequency(Lemma lemma) {
        Lemma lemmaDB = repository.findLemmaByLemmaAndSite(lemma.getLemma(), lemma.getSite());
        lemmaDB.setFrequency(lemmaDB.getFrequency() + 1);
        repository.save(lemmaDB);
    }

    @Override
    public synchronized List<Lemma> getLemmasByLemma(String lemma) {
        return repository.findLemmasByLemma(lemma);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public synchronized void decrementFrequency(Lemma lemma) {
        Lemma lemmaDB = repository.findLemmaByLemmaAndSite(lemma.getLemma(), lemma.getSite());
        if (lemmaDB.getFrequency() != 0) {
            lemmaDB.setFrequency(lemmaDB.getFrequency() - 1);
        }
        repository.save(lemmaDB);
    }
    @Override
    public Lemma getLemmaByLemmaAndSite(String lemma, Site site) {
        return repository.findLemmaByLemmaAndSite(lemma, site);
    }
}
