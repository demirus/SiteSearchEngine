package ru.belkov.SiteSearchEngine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.belkov.SiteSearchEngine.model.entity.Page;
import ru.belkov.SiteSearchEngine.model.entity.Site;

public interface SiteRepository extends JpaRepository<Site, Integer> {
    boolean existsByUrl(String url);
    Site getByUrl(String url);
    void deleteByUrl(String url);
    void deleteAll();
}
