package com.nbsp.translator.api;

import com.nbsp.translator.models.yandextranslator.Language;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nickolay on 13.09.15.
 */

public class Languages {
    private static final String[] LANGUAGES = {"ru", "en", "af", "ar", "az", "be", "bg",
            "bs", "ca", "cs", "cy", "da", "de", "el", "es", "et", "eu", "fa", "fi", "fr",
            "ga", "gl", "he", "hr", "ht", "hu", "hy", "id", "is", "it", "ja", "ka", "kk",
            "ko", "ky", "la", "lt", "lv", "mg", "mk", "mn", "ms", "mt", "nl", "no", "pl",
            "pt", "ro", "sk", "sl", "sq", "sr", "sv", "sw", "tg", "th", "tl", "tr", "tt",
            "uk", "uz", "vi", "zh"};

    private static Languages ourInstance = new Languages();
    private List<Language> mLanguages;

    public static Languages getInstance() {
        return ourInstance;
    }

    private Languages() {
        mLanguages = new ArrayList<>();
        for(String lang : LANGUAGES) {
            mLanguages.add(new Language(lang));
        }
    }

    public List<Language> getLanguages() {
        return mLanguages;
    }
}
