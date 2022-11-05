package ru.belkov.SiteSearchEngine.services;

import org.springframework.stereotype.Service;
import ru.belkov.SiteSearchEngine.model.entity.Page;
import ru.belkov.SiteSearchEngine.repository.PageRepository;

@Service
public class PageServiceImpl implements PageService {

    private PageRepository pageRepository;

    public PageServiceImpl(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    @Override
    public synchronized boolean addIfNotExists(Page page) {
        if (!pageRepository.existsByPathAndSite(page.getPath(), page.getSite())) {
            pageRepository.saveAndFlush(page);
            return true;
        }
        return false;
    }

    @Override
    public Page updateByPathAndSite(Page page) {
        Page dbPage = pageRepository.getByPathAndSite(page.getPath(), page.getSite());
        dbPage.setCode(page.getCode());
        dbPage.setContent(page.getContent());
        return pageRepository.saveAndFlush(dbPage);
    }

    @Override
    public long count() {
        return pageRepository.count();
    }

    @Override
    public Page getByUrl(String url) {
        return pageRepository.getByPath(url);
    }
}