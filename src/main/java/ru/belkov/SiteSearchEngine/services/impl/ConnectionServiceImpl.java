package ru.belkov.SiteSearchEngine.services.impl;

import org.jsoup.Connection;
import org.springframework.stereotype.Service;
import ru.belkov.SiteSearchEngine.config.SiteParserConfig;
import ru.belkov.SiteSearchEngine.model.SiteConnectionManager;
import ru.belkov.SiteSearchEngine.model.SiteConnectionManagerImpl;
import ru.belkov.SiteSearchEngine.model.entity.Site;
import ru.belkov.SiteSearchEngine.services.ConnectionService;
import ru.belkov.SiteSearchEngine.services.SiteService;

import java.util.HashMap;
import java.util.Map;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    Map<Site, SiteConnectionManager> siteConnectionManagerMap = new HashMap<>();
    SiteParserConfig siteParserConfig;
    SiteService siteService;

    public ConnectionServiceImpl(SiteParserConfig siteParserConfig, SiteService siteService) {
        this.siteParserConfig = siteParserConfig;
        this.siteService = siteService;
    }

    @Override
    public Connection.Response getResponse(Site site, String url) {
        SiteConnectionManager manager = siteConnectionManagerMap.get(site);
        if (manager != null) {
            return manager.getResponse(url, siteParserConfig);
        } else {
            SiteConnectionManager siteConnectionManager = new SiteConnectionManagerImpl();
            siteConnectionManagerMap.put(site, siteConnectionManager);
            return siteConnectionManager.getResponse(url, siteParserConfig);
        }
    }
}
