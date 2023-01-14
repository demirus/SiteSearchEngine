package ru.belkov.SiteSearchEngine.model;

public class StartParsingWorker implements Runnable {
    private SiteManager siteManager;
    public StartParsingWorker(SiteManager siteManager) {
        this.siteManager = siteManager;
    }

    @Override
    public void run() {
        siteManager.startParsing();
    }
}
