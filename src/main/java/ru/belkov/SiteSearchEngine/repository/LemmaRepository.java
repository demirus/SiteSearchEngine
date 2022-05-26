package ru.belkov.SiteSearchEngine.repository;

import org.springframework.data.repository.CrudRepository;
import ru.belkov.SiteSearchEngine.model.entity.Lemma;

public interface LemmaRepository extends CrudRepository<Lemma, Integer> {

}
