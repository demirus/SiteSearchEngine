package ru.belkov.SiteSearchEngine.util.lemasUtil;

import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;

public class SearchEngineMorphologyRussian extends RussianLuceneMorphology implements SearchEngineMorphology {

    public SearchEngineMorphologyRussian() throws IOException {
    }
}
