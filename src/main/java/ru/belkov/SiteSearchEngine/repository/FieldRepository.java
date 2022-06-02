package ru.belkov.SiteSearchEngine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.belkov.SiteSearchEngine.model.entity.Field;

public interface FieldRepository extends JpaRepository<Field, Integer> {
}
