package ru.belkov.SiteSearchEngine.model.entity;

import java.util.List;

public class SearchResponse {
    public boolean result;
    public int count;
    public List<SearchDataObject> data;

    public SearchResponse() {
    }

    public SearchResponse(boolean result, int count, List<SearchDataObject> data) {
        this.result = result;
        this.count = count;
        this.data = data;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<SearchDataObject> getData() {
        return data;
    }

    public void setData(List<SearchDataObject> data) {
        this.data = data;
    }
}
