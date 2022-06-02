package ru.belkov.SiteSearchEngine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.belkov.SiteSearchEngine.model.entity.Lemma;

public interface LemmaRepository extends JpaRepository<Lemma, Integer> {
    boolean existsLemmaByLemma(String lemma);

    Lemma findLemmaByLemma(String lemma);
}
