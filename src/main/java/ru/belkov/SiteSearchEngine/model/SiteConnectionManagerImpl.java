package ru.belkov.SiteSearchEngine.model;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.belkov.SiteSearchEngine.config.SiteParserConfig;
import ru.belkov.SiteSearchEngine.services.impl.SearchServiceImpl;

import java.io.IOException;

public class SiteConnectionManagerImpl implements SiteConnectionManager {
    private static final Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);
    private Long lastTime = 0L;
    private int countOfAttempts = 5;

    @Override
    public synchronized Connection.Response getResponse(String url, SiteParserConfig siteParserConfig) {
        try {
            if (System.currentTimeMillis() - lastTime >= siteParserConfig.getIntervalBetweenRequests()) {
                Connection.Response response = getResponse(url, siteParserConfig.getUserAgent(), siteParserConfig.getReferrer());
                lastTime = System.currentTimeMillis();
                countOfAttempts = 5;
                return response;
            } else {
                Thread.sleep(siteParserConfig.getIntervalBetweenRequests() - (System.currentTimeMillis() - lastTime));
                Connection.Response response = getResponse(url, siteParserConfig.getUserAgent(), siteParserConfig.getReferrer());
                lastTime = System.currentTimeMillis();
                countOfAttempts = 5;
                return response;
            }
        } catch (IOException e) {
            logger.error("IOException url: " + url + " " + e.getMessage(), e);
            if (countOfAttempts > 0) {
                try {
                    Thread.sleep(getDelayBetweenAttempts());
                } catch (InterruptedException ex) {
                    logger.error(e.getMessage(), e);
                }
                countOfAttempts--;
                getResponse(url, siteParserConfig);
            }
            countOfAttempts = 5;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    private long getDelayBetweenAttempts() {
        switch (countOfAttempts) {
            case 5:
                return 2000L;
            case 4:
                return 10_000L;
            case 3:
                return 60_000L;
            case 2:
                return 300_000L;
            case 1:
                return 600_000L;
        }
        return 60_000L;
    }

    private Connection.Response getResponse(String url, String userAgent, String referrer) throws IOException {
        return Jsoup.connect(url)
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .userAgent(userAgent)
                .referrer(referrer)
                .execute();
    }
}
