package ru.belkov.SiteSearchEngine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.belkov.SiteSearchEngine.model.entity.Lemma;
import ru.belkov.SiteSearchEngine.model.entity.Site;

import java.util.List;

public interface LemmaRepository extends JpaRepository<Lemma, Integer> {
    boolean existsLemmaByLemmaAndSite(String lemma, Site site);

    Lemma findLemmaByLemmaAndSite(String lemma, Site site);

    List<Lemma> findLemmasByLemma(String lemma);
}
