package ru.belkov.SiteSearchEngine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.belkov.SiteSearchEngine.model.entity.Page;

public interface PageRepository extends JpaRepository<Page, Integer> {
    boolean existsByPath(String path);

    Page getByPath(String path);
}
