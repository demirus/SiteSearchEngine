package ru.belkov.SiteSearchEngine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.belkov.SiteSearchEngine.model.entity.Page;
import ru.belkov.SiteSearchEngine.model.entity.Site;

public interface PageRepository extends JpaRepository<Page, Integer> {
    boolean existsByPathAndSite(String path, Site site);

    Page getByPathAndSite(String path, Site site);

    Page getByPath(String url);
}
