package com.nbsp.translator.api;

import com.nbsp.translator.models.Language;
import com.nbsp.translator.models.TranslationDirection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nickolay on 13.09.15.
 */

public class Languages {
    private static final String[] LANGUAGES = {"ru", "en", "be", "da", "de", "fr", "ja", "uk", "zh", "az"};
    private static Languages ourInstance = new Languages();
    private List<Language> mLanguages;
    private TranslationDirection mTranslationDirection;

    public static Languages getInstance() {
        return ourInstance;
    }

    private Languages() {
        mLanguages = new ArrayList<>();
        for(String lang : LANGUAGES) {
            mLanguages.add(new Language(lang));
        }

        // Default direction
        // TODO: detect current language
        mTranslationDirection = new TranslationDirection(
                mLanguages.get(0),
                mLanguages.get(1)
        );
    }

    public List<Language> getLanguages() {
        return mLanguages;
    }

    public TranslationDirection getTranslationDirection() {
        return mTranslationDirection;
    }
}
