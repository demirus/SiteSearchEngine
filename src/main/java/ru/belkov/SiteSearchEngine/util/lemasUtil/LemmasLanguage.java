package ru.belkov.SiteSearchEngine.util.lemasUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class LemmasLanguage {
    private final String languagePattern;

    private final SearchEngineMorphology searchEngineMorphology;

    private final Set<RedundantPartsOfSpeech> redundantPartsOfSpeech;

    public LemmasLanguage(String languagePattern, SearchEngineMorphology searchEngineMorphology, Set<RedundantPartsOfSpeech> redundantPartsOfSpeech) throws IOException {
        this.languagePattern = languagePattern;
        this.searchEngineMorphology = searchEngineMorphology;
        this.redundantPartsOfSpeech = redundantPartsOfSpeech;
    }

    public List<String> checkWord(String inputWord) {
        String word = inputWord.replaceAll(languagePattern, "");
        if (!word.equals("")) {
            List<String> morphInfo = searchEngineMorphology.getMorphInfo(word);
            for (String info : morphInfo) {
                if (!isRedundantPartOfSpeech(info)) {
                    return searchEngineMorphology.getNormalForms(word);
                }
            }
        }
        return new ArrayList<>();
    }

    private boolean isRedundantPartOfSpeech(String word) {
        for (RedundantPartsOfSpeech redundantPartsOfSpeech : redundantPartsOfSpeech) {
            if (word.matches(".*" + redundantPartsOfSpeech.name() + ".*")) {
                return true;
            }
        }
        return false;
    }
}
