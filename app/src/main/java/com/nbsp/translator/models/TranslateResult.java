package com.nbsp.translator.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Dimorinny on 10.09.15.
 */
public class TranslateResult {

    @SerializedName("code")
    private int mCode;

    @SerializedName("lang")
    private String mLang;

    @SerializedName("text")
    private List<String> mTexts;

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
}
