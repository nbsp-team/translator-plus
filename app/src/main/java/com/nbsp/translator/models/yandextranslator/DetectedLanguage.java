package com.nbsp.translator.models.yandextranslator;

import com.google.gson.annotations.SerializedName;
import com.nbsp.translator.models.Language;

/**
 * Created by nickolay on 26.09.15.
 */

public class DetectedLanguage {
    @SerializedName("lang")
    private String code;

    public DetectedLanguage(String lang) {
        this.code = lang;
    }

    public String getCode() {
        return code;
    }

    public Language getLanguage() {
        if (code != null && code.length() > 0) {
            return new Language(code);
        } else {
            return null;
        }
    }
}
