package com.nbsp.translator.models.yandextranslator;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Dimorinny on 10.09.15.
 */
public class TranslateResult {

    @SerializedName("code")
    private final int mCode;

    @SerializedName("lang")
    private final String mLang;

    @SerializedName("text")
    private final List<String> mTexts;

    @SerializedName("detected")
    private DetectedLanguage detectedLang;

    public TranslateResult(int code, String lang, List<String> texts) {
        super();

        mCode = code;
        mLang = lang;
        mTexts = texts;
    }

    public int getCode() {
        return mCode;
    }

    public String getLang() {
        return mLang;
    }

    public List<String> getTexts() {
        return mTexts;
    }

    public String getText() {
        if (mTexts.size() > 0) {
            return mTexts.get(0);
        } else {
            return "";
        }
    }

    public DetectedLanguage getDetectedLang() {
        return detectedLang;
    }
}
