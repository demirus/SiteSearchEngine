package ru.belkov.SiteSearchEngine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.belkov.SiteSearchEngine.model.entity.Index;
import ru.belkov.SiteSearchEngine.model.entity.Lemma;
import ru.belkov.SiteSearchEngine.model.entity.Page;

public interface IndexRepository extends JpaRepository<Index, Integer> {
    Index findIndexByLemmaAndPage(Lemma lemma, Page page);
}
