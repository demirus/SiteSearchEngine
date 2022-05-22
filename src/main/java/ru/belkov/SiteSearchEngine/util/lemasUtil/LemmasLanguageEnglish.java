package ru.belkov.SiteSearchEngine.util.lemasUtil;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class LemmasLanguageEnglish extends LemmasLanguage {

    static Set<RedundantPartsOfSpeech> excludedPartsOfSpeech = new HashSet<>(){{
        add(RedundantPartsOfSpeech.INT);
        add(RedundantPartsOfSpeech.PART);
        add(RedundantPartsOfSpeech.CONJ);
        add(RedundantPartsOfSpeech.PREP);
    }};

    public LemmasLanguageEnglish() throws IOException {
        super("[^A-Za-zÀ-ÿ]", new SearchEngineMorphologyEnglish(), excludedPartsOfSpeech);
    }
}
