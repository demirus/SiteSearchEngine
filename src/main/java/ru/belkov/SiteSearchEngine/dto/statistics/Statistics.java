package ru.belkov.SiteSearchEngine.dto.statistics;

import java.util.List;

public class Statistics {
    private TotalStatistics total;
    private List<SiteInformation> detailed;

    public Statistics() {
    }

    public TotalStatistics getTotal() {
        return total;
    }

    public void setTotal(TotalStatistics total) {
        this.total = total;
    }

    public List<SiteInformation> getDetailed() {
        return detailed;
    }

    public void setDetailed(List<SiteInformation> detailed) {
        this.detailed = detailed;
    }
}
