package ru.belkov.SiteSearchEngine.util.lemasUtil;

import java.util.List;

public interface SearchEngineMorphology {
    List<String> getNormalForms(String var1);

    List<String> getMorphInfo(String var1);
}
