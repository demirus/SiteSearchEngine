package ru.belkov.SiteSearchEngine.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("search")
public class SearchServiceConfig {
    private Long maxLemmaFrequency;

    public long getMaxLemmaFrequency() {
        return maxLemmaFrequency;
    }

    public void setMaxLemmaFrequency(long maxLemmaFrequency) {
        this.maxLemmaFrequency = maxLemmaFrequency;
    }
}
