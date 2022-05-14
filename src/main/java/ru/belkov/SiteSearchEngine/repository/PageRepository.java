package ru.belkov.SiteSearchEngine.repository;

import org.springframework.data.repository.CrudRepository;
import ru.belkov.SiteSearchEngine.model.entity.Page;

public interface PageRepository extends CrudRepository<Page, Integer> {
}
