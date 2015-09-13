package com.nbsp.translator.models;

import android.util.Pair;

/**
 * Created by nickolay on 13.09.15.
 */

public class TranslationDirection extends Pair<Language, Language> {
    /**
     * Constructor for a translation pair.
     *
     * @param from language
     * @param to language
     */
    public TranslationDirection(Language from, Language to) {
        super(from, to);
    }

    @Override
    public String toString() {
        return String.format("%s-%s", first.getYandexCode(), second.getYandexCode());
    }
}
