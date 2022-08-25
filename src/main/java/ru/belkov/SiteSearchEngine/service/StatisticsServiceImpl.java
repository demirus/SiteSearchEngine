package ru.belkov.SiteSearchEngine.service;

import org.springframework.stereotype.Service;
import ru.belkov.SiteSearchEngine.model.entity.Site;
import ru.belkov.SiteSearchEngine.model.entity.SiteInformation;
import ru.belkov.SiteSearchEngine.model.entity.Statistics;
import ru.belkov.SiteSearchEngine.model.entity.TotalStatistics;

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
        totalStatistics.setIndexing(siteParserService.isIndexing());
        totalStatistics.setLemmas(lemmaService.count());
        totalStatistics.setPages(pageService.count());
        totalStatistics.setSites(siteService.count());
        return totalStatistics;
    }

    private List<SiteInformation> getSitesInformation() {
        List<SiteInformation> siteInformationList = new ArrayList<>();
        List<Site> sites = siteService.getAll();
        for (Site site : sites) {
            SiteInformation siteInformation = new SiteInformation();
            siteInformation.setStatus(site.getStatus().toString());
            siteInformation.setStatusTime(site.getStatusTime().toString());
            siteInformation.setError(site.getLastError());
            siteInformation.setUrl(site.getUrl());
            siteInformation.setName(site.getName());
            siteInformation.setLemmas(String.valueOf(site.getLemmas().size()));
            siteInformation.setPages(String.valueOf(site.getPages().size()));
            siteInformationList.add(siteInformation);
        }
        return siteInformationList;
    }
}
