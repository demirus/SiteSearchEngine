package ru.belkov.SiteSearchEngine.model;

import ru.belkov.SiteSearchEngine.model.entity.Site;

public interface SiteManager {
    void startParsing();
    Site getSite();
    void stopParsing();
    boolean isStop();
    void deleteSite();
    void stopParsing(String error);
}
