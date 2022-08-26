package ru.belkov.SiteSearchEngine.model.entity;

public class SiteInformationWithErrorImpl extends SiteInformation {
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
