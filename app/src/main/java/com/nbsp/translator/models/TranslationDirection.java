package com.nbsp.translator.models;

import com.nbsp.translator.models.yandextranslator.Language;

import java.io.Serializable;

/**
 * Created by nickolay on 13.09.15.
 */

public class TranslationDirection implements Serializable {
    private Language from;
    private Language to;

    public TranslationDirection(String direction) {
        String[] codes = direction.split("-");
        this.from = new Language(codes[0]);
        this.to = new Language(codes[1]);
    }

    public TranslationDirection(Language from, Language to) {
        this.from = from;
        this.to = to;
    }

    public Language getFrom() {
        return from;
    }

    public void setFrom(Language from) {
        this.from = from;
    }

    public Language getTo() {
        return to;
    }

    public void setTo(Language to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return String.format("%s-%s", getFrom().getLanguageCode(), getTo().getLanguageCode());
    }

    public void swap() {
        Language temp = to;
        to = from;
        from = temp;
    }
}
