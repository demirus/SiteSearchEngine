package ru.belkov.SiteSearchEngine.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.belkov.SiteSearchEngine.dto.Response;
import ru.belkov.SiteSearchEngine.dto.statistics.*;
import ru.belkov.SiteSearchEngine.model.entity.*;
import ru.belkov.SiteSearchEngine.services.LemmaService;
import ru.belkov.SiteSearchEngine.services.PageService;
import ru.belkov.SiteSearchEngine.services.SiteService;
import ru.belkov.SiteSearchEngine.services.StatisticsService;

import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    private final SiteParserServiceImpl siteParserService;
    private final LemmaService lemmaService;

    private final PageService pageService;

    private final SiteService siteService;
    private final Logger logger = LoggerFactory.getLogger(StatisticsServiceImpl.class);

    public StatisticsServiceImpl(SiteParserServiceImpl siteParserService, LemmaService lemmaService, PageService pageService, SiteService siteService) {
        this.siteParserService = siteParserService;
        this.lemmaService = lemmaService;
        this.pageService = pageService;
        this.siteService = siteService;
    }

    @Override
    public Response getStatistics() {
        try {
            Statistics statistics = new Statistics();
            statistics.setTotal(getTotalStatistics());
            statistics.setDetailed(getSitesInformation());
            StatisticsResponse response = new StatisticsResponse(Boolean.TRUE, null, HttpStatus.OK);
            response.setStatistics(statistics);
            return response;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new Response(Boolean.FALSE, "Непредвиденная ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private TotalStatistics getTotalStatistics() {
        TotalStatistics totalStatistics = new TotalStatistics();
        totalStatistics.setIsIndexing(siteParserService.isFullIndexing());
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
                error = "";
            }
            SiteInformation siteInformation = new SiteInformation();
            siteInformation.setStatus(site.getStatus() != null ? site.getStatus().toString() : "");
            siteInformation.setStatusTime(site.getStatusTime() != null ? site.getStatusTime().toString() : "");
            siteInformation.setError(error.equals("") ? null : error);
            siteInformation.setUrl(site.getUrl() != null ? site.getUrl() : "");
            siteInformation.setName(site.getName() != null ? site.getName() : "");
            siteInformation.setLemmas(String.valueOf(site.getLemmas().size()));
            siteInformation.setPages(String.valueOf(site.getPages().size()));
            siteInformationWithoutErrorImplList.add(siteInformation);
        }
        return siteInformationWithoutErrorImplList;
    }
}
