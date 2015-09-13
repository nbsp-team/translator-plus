package com.nbsp.translator.models;

import android.util.Pair;

import java.io.Serializable;

/**
 * Created by nickolay on 13.09.15.
 */

public class TranslationDirection implements Serializable {
    private Language from;
    private Language to;

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
        return String.format("%s-%s", getFrom().getYandexCode(), getTo().getYandexCode());
    }
}
