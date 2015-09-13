package com.nbsp.translator.models;

import java.util.Locale;

/**
 * Created by nickolay on 13.09.15.
 */

public class Language {
    private String name;
    private String yandexCode;
    private Locale locale;

    public Language(String name, String yandexCode, Locale locale) {
        this.name = name;
        this.yandexCode = yandexCode;
        this.locale = locale;
    }

    public String getName() {
        return name;
    }

    public String getYandexCode() {
        return yandexCode;
    }

    public Locale getLocale() {
        return locale;
    }

    public String getLanguageCode() {
        return locale.toLanguageTag();
    }
}
