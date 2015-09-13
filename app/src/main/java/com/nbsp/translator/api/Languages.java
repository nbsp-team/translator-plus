package com.nbsp.translator.api;

import android.content.Context;

import com.nbsp.translator.models.Language;
import com.nbsp.translator.models.TranslationDirection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nickolay on 13.09.15.
 */

public class Languages {
    private static Languages ourInstance = new Languages();
    private List<Language> mLanguages;
    private TranslationDirection mTranslationDirection;

    public static Languages getInstance() {
        return ourInstance;
    }

    private Languages() {
        mLanguages = new ArrayList<>();
        mLanguages.add(new Language("ru"));
        mLanguages.add(new Language("en"));
        mLanguages.add(new Language("be"));
        mLanguages.add(new Language("da"));
        mLanguages.add(new Language("de"));
        mLanguages.add(new Language("fr"));
        mLanguages.add(new Language("ja"));
        mLanguages.add(new Language("uk"));
        mLanguages.add(new Language("zh"));

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
