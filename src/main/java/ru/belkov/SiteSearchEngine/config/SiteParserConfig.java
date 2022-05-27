package ru.belkov.SiteSearchEngine.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.belkov.SiteSearchEngine.model.Site;
import ru.belkov.SiteSearchEngine.model.entity.Field;
import ru.belkov.SiteSearchEngine.repository.FieldRepository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties("parser")
public class SiteParserConfig {
    private List<Site> sites = new ArrayList<>();

    private String userAgent;

    private String referrer;

    private final FieldRepository fieldRepository;

    private Iterable<Field> fields;

    @Autowired
    public SiteParserConfig(FieldRepository fieldRepository) {
        this.fieldRepository = fieldRepository;
    }

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

    public Iterable<Field> getFields() {
        return fields;
    }

    @Override
    public String toString() {
        return "SiteParserConfig{" +
                "sites=" + sites +
                '}';
    }

    @PostConstruct
    private void initFields() {
        fields = fieldRepository.findAll();
    }
}
