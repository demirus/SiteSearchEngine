package ru.belkov.SiteSearchEngine.repository;

import org.springframework.data.repository.CrudRepository;
import ru.belkov.SiteSearchEngine.model.entity.Index;

public interface IndexRepository extends CrudRepository<Index, Integer> {

}
