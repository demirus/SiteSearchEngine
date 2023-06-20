package ru.belkov.SiteSearchEngine.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.belkov.SiteSearchEngine.model.SiteParser;
import ru.belkov.SiteSearchEngine.model.entity.Site;
import ru.belkov.SiteSearchEngine.services.SiteService;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties("parser")
public class SiteParserConfig {
    private List<Map<String, String>> configSites;

    private String userAgent;

    private String referrer;

    private Long intervalBetweenRequests;

    private boolean autoStart;

    private final SiteService siteService;

    private static final Logger logger = LoggerFactory.getLogger(SiteParserConfig.class);


    @Autowired
    public SiteParserConfig(SiteService siteService) {
        this.siteService = siteService;
    }

    @PostConstruct
    private void initSites() throws InstantiationException {
        for (Map<String, String> configSite : configSites) {
            Site site = new Site();
            String name = configSite.get("name");
            String url = configSite.get("url");
            if (name == null || url == null) {
                logger.error("Missing argument in configSites needs name or url");
                throw new InstantiationException("Missing argument in configSites needs name or url");
            }
            site.setName(name);
            site.setUrl(url);
            site.setStatusTime(new Timestamp(System.currentTimeMillis()));
            siteService.addIfNotExists(site);
        }
    }

    public void setConfigSites(List<Map<String, String>> configSites) {
        this.configSites = configSites;
    }

    public List<Map<String, String>> getConfigSites() {
        return configSites;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public Long getIntervalBetweenRequests() {
        return intervalBetweenRequests;
    }

    public void setIntervalBetweenRequests(Long intervalBetweenRequests) {
        this.intervalBetweenRequests = intervalBetweenRequests;
    }
    public boolean isAutoStart() {
        return autoStart;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }
}
