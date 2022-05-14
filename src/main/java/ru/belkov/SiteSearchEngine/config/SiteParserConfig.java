package ru.belkov.SiteSearchEngine.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.belkov.SiteSearchEngine.model.Site;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties("parser")
public class SiteParserConfig {
    private List<Site> sites = new ArrayList<>();

    private String userAgent;

    private String referrer;

    public List<Site> getSites() {
        return sites;
    }

    public void setSites(List<Site> sites) {
        this.sites = sites;
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

    @Override
    public String toString() {
        return "SiteParserConfig{" +
                "sites=" + sites +
                '}';
    }
}
