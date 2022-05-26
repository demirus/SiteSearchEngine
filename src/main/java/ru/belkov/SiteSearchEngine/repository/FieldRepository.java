package ru.belkov.SiteSearchEngine.repository;

import org.springframework.data.repository.CrudRepository;
import ru.belkov.SiteSearchEngine.model.entity.Field;

public interface FieldRepository extends CrudRepository<Field, Integer> {
}
