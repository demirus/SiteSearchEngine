package ru.belkov.SiteSearchEngine.dto.search;

import org.springframework.http.HttpStatus;
import ru.belkov.SiteSearchEngine.dto.Response;

import java.util.List;

public class SearchResponse extends Response {
    private int count;
    private List<SearchDataObject> data;

    public SearchResponse(Boolean result, String error, HttpStatus httpStatus, int count, List<SearchDataObject> data) {
        super(result, error, httpStatus);
        this.count = count;
        this.data = data;
    }

    public SearchResponse(Boolean result, HttpStatus httpStatus) {
        super(result, null, httpStatus);
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
