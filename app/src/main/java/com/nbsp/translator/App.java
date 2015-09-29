package com.nbsp.translator;

import com.nbsp.translator.models.Language;
import com.nbsp.translator.models.TranslationDirection;

import java.util.List;

/**
 * Created by nickolay on 13.09.15.
 */

public class App {
    private final static App ourInstance = new App();
    private TranslationDirection mTranslationDirection;

    public static App getInstance() {
        return ourInstance;
    }

    private App() {
        List<Language> languages = Languages.getInstance().getLanguages();
        mTranslationDirection = new TranslationDirection(
                languages.get(0),
                languages.get(1)
        );
    }

    public void setTranslationDirection(TranslationDirection direction) {
        mTranslationDirection = direction;
    }

    public TranslationDirection getTranslationDirection() {
        return mTranslationDirection;
    }
}
