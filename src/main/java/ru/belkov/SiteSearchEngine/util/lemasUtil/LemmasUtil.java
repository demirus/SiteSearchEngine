package ru.belkov.SiteSearchEngine.util.lemasUtil;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LemmasUtil {
    public static Map<String, Integer> getLemmas(String inputText, List<LemmasLanguage> lemmasLanguages) {
        Map<String, Integer> lemmasMap = new HashMap<>();
        try {
            StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(inputText));
            int currentToken = tokenizer.nextToken();
            while (currentToken != StreamTokenizer.TT_EOF) {
                if (tokenizer.ttype == StreamTokenizer.TT_WORD) {
                    String word = tokenizer.sval.toLowerCase(Locale.ROOT);
                    for (LemmasLanguage lemmasLanguage : lemmasLanguages) {
                        List<String> normalForms = lemmasLanguage.checkWord(word);
                        for (String normalForm : normalForms) {
                            if (lemmasMap.containsKey(normalForm)) {
                                lemmasMap.put(normalForm, lemmasMap.get(normalForm) + 1);
                            } else {
                                lemmasMap.put(normalForm, 1);
                            }
                        }
                    }
                }
                currentToken = tokenizer.nextToken();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return lemmasMap;
    }
}
