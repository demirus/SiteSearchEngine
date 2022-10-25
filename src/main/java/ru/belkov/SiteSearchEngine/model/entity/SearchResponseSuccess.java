package ru.belkov.SiteSearchEngine.model.entity;

import org.springframework.http.HttpStatus;

import java.util.List;

public class SearchResponseSuccess extends SearchResponse {
    public int count;
    public List<SearchDataObject> data;

    public SearchResponseSuccess(boolean result, HttpStatus httpStatus) {
        super(result, httpStatus);
    }

    public SearchResponseSuccess(boolean result, int count, List<SearchDataObject> data, HttpStatus httpStatus) {
        super(result, httpStatus);
        this.count = count;
        this.data = data;
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
