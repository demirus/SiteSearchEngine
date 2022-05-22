package ru.belkov.SiteSearchEngine.util.lemasUtil;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class LemmasLanguageRussian extends LemmasLanguage {
    static Set<RedundantPartsOfSpeech> excludedPartsOfSpeech = new HashSet<>(){{
        add(RedundantPartsOfSpeech.МЕЖД);
        add(RedundantPartsOfSpeech.СОЮЗ);
        add(RedundantPartsOfSpeech.ПРЕДЛ);
        add(RedundantPartsOfSpeech.ЧАСТ);
    }};

    public LemmasLanguageRussian() throws IOException {
        super("[^а-яА-ЯёЁ]", new SearchEngineMorphologyRussian(), excludedPartsOfSpeech);
    }
}
