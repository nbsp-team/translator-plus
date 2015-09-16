package com.nbsp.translator.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Locale;

/**
 * Created by nickolay on 13.09.15.
 */

public class Language implements Serializable {
//    private Locale locale;

    @SerializedName("lang")
    private String code;

    public Language(String code) {
        this.code = code;
    }

    public String getName() {
        return getLocale().getDisplayName(Locale.getDefault());
    }

    public String getYandexCode() {
        return code;
    }

    public Locale getLocale() {
        return Locale.forLanguageTag(code);
    }

    public String getLanguageCode() {
        return code;
    }
}
