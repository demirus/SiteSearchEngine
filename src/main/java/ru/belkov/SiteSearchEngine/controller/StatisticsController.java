package ru.belkov.SiteSearchEngine.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class StatisticsController {
    class Statistics {
        private Total total = new Total();
        private List<SiteInformation> detailed = new ArrayList<>(){{
            add(new SiteInformation());
        }};

        public Total getTotal() {
            return total;
        }

        public void setTotal(Total total) {
            this.total = total;
        }

        public List<SiteInformation> getDetailed() {
            return detailed;
        }

        public void setDetailed(List<SiteInformation> detailed) {
            this.detailed = detailed;
        }
    }

    class SiteInformation {
        private String url = "http://www.site.com";
        private String name = "Имя сайта";
        private String status = "INDEXED";
        private String statusTime = "1600160357";
        private String error = "Ошибка индексации: главная страница сайта недоступна";
        private String pages = "5764";
        private String lemmas = "321115";

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStatusTime() {
            return statusTime;
        }

        public void setStatusTime(String statusTime) {
            this.statusTime = statusTime;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getPages() {
            return pages;
        }

        public void setPages(String pages) {
            this.pages = pages;
        }

        public String getLemmas() {
            return lemmas;
        }

        public void setLemmas(String lemmas) {
            this.lemmas = lemmas;
        }
    }

    class Total {
        private int sites = 10;
        private int pages = 436423;
        private int lemmas = 5127891;
        private boolean isIndexing = true;

        public int getSites() {
            return sites;
        }

        public void setSites(int sites) {
            this.sites = sites;
        }

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }

        public int getLemmas() {
            return lemmas;
        }

        public void setLemmas(int lemmas) {
            this.lemmas = lemmas;
        }

        public boolean isIndexing() {
            return isIndexing;
        }

        public void setIndexing(boolean indexing) {
            isIndexing = indexing;
        }
    }

    @GetMapping("/statistics")
    public Map<String, Object> statistics() {
        Map<String, Object> result = new HashMap<>();
        result.put("result", "true");
        result.put("statistics", new Statistics());
        return result;
    }
}
