package ru.belkov.SiteSearchEngine.services;

import org.springframework.stereotype.Service;
import ru.belkov.SiteSearchEngine.dto.statistics.*;
import ru.belkov.SiteSearchEngine.model.entity.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    private final SiteParserService siteParserService;
    private final LemmaService lemmaService;

    private final PageService pageService;

    private final SiteService siteService;

    public StatisticsServiceImpl(SiteParserService siteParserService, LemmaService lemmaService, PageService pageService, SiteService siteService) {
        this.siteParserService = siteParserService;
        this.lemmaService = lemmaService;
        this.pageService = pageService;
        this.siteService = siteService;
    }

    @Override
    public Statistics getStatistics() {
        Statistics statistics = new Statistics();
        statistics.setTotal(getTotalStatistics());
        statistics.setDetailed(getSitesInformation());
        return statistics;
    }

    private TotalStatistics getTotalStatistics() {
        TotalStatistics totalStatistics = new TotalStatistics();
        totalStatistics.setIsIndexing(siteParserService.isIndexing());
        totalStatistics.setLemmas(lemmaService.count());
        totalStatistics.setPages(pageService.count());
        totalStatistics.setSites(siteService.count());
        return totalStatistics;
    }

    private List<SiteInformation> getSitesInformation() {
        List<SiteInformation> siteInformationWithoutErrorImplList = new ArrayList<>();
        List<Site> sites = siteService.getAll();
        for (Site site : sites) {
            String error = site.getLastError();
            if (error == null) {
                SiteInformationWithoutErrorImpl siteInformationWithoutErrorImpl = new SiteInformationWithoutErrorImpl();
                siteInformationWithoutErrorImpl.setStatus(site.getStatus().toString());
                siteInformationWithoutErrorImpl.setStatusTime(site.getStatusTime().toString());
                siteInformationWithoutErrorImpl.setUrl(site.getUrl());
                siteInformationWithoutErrorImpl.setName(site.getName());
                siteInformationWithoutErrorImpl.setLemmas(String.valueOf(site.getLemmas().size()));
                siteInformationWithoutErrorImpl.setPages(String.valueOf(site.getPages().size()));
                siteInformationWithoutErrorImplList.add(siteInformationWithoutErrorImpl);
            } else {
                SiteInformationWithErrorImpl siteInformationWithErrorImpl = new SiteInformationWithErrorImpl();
                siteInformationWithErrorImpl.setStatus(site.getStatus().toString());
                siteInformationWithErrorImpl.setStatusTime(site.getStatusTime().toString());
                siteInformationWithErrorImpl.setError(error);
                siteInformationWithErrorImpl.setUrl(site.getUrl());
                siteInformationWithErrorImpl.setName(site.getName());
                siteInformationWithErrorImpl.setLemmas(String.valueOf(site.getLemmas().size()));
                siteInformationWithErrorImpl.setPages(String.valueOf(site.getPages().size()));
                siteInformationWithoutErrorImplList.add(siteInformationWithErrorImpl);
            }

        }
        return siteInformationWithoutErrorImplList;
    }
}
