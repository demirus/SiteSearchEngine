package ru.belkov.SiteSearchEngine.dto.statistics;

import ru.belkov.SiteSearchEngine.dto.statistics.SiteInformation;

public class SiteInformationWithErrorImpl extends SiteInformation {
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
