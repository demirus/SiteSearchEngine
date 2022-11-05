package ru.belkov.SiteSearchEngine.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.belkov.SiteSearchEngine.enums.SiteStatus;
import ru.belkov.SiteSearchEngine.model.entity.Site;
import ru.belkov.SiteSearchEngine.model.entity.Field;
import ru.belkov.SiteSearchEngine.repository.FieldRepository;
import ru.belkov.SiteSearchEngine.services.SiteService;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties("parser")
public class SiteParserConfig {
    private List<Site> sites = new ArrayList<>();

    private Map<String, String> initSites;

    private String userAgent;

    private String referrer;

    private final FieldRepository fieldRepository;

    private final SiteService siteService;

    private Iterable<Field> fields;

    @Autowired
    public SiteParserConfig(FieldRepository fieldRepository, SiteService siteService) {
        this.fieldRepository = fieldRepository;
        this.siteService = siteService;
    }

    @PostConstruct
    private void initFields() {
        fields = fieldRepository.findAll();
    }

    @PostConstruct
    private void initSites() {
        for (Map.Entry<String, String> entry : initSites.entrySet()) {
            Site site = new Site();
            site.setUrl(entry.getValue());
            site.setName(entry.getKey());
            site.setStatusTime(new Timestamp(System.currentTimeMillis()));
            site.setStatus(SiteStatus.INDEXING);
            sites.add(site);
        }
    }

    public void setInitSites(Map<String, String> initSites) {
        this.initSites = initSites;
    }

    public Map<String, String> getInitSites() {
        return initSites;
    }

    public void setSites(Map<String, String> initSites) {
        this.initSites = initSites;
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

    public Iterable<Field> getFields() {
        return fields;
    }

    public List<Site> getSites() {
        return sites;
    }

    public void setSites(List<Site> sites) {
        this.sites = sites;
    }
}
