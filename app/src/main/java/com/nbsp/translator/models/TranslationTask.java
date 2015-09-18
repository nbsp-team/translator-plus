package com.nbsp.translator.models;

/**
 * Created by nickolay on 18.09.15.
 */

public class TranslationTask {
    private final String mTextToTranslate;
    private final TranslationDirection mTranslationDirection;

    public TranslationTask(String textToTranslate, TranslationDirection translationDirection) {
        mTextToTranslate = textToTranslate;
        mTranslationDirection = translationDirection;
    }

    public String getTextToTranslate() {
        return mTextToTranslate;
    }

    public TranslationDirection getTranslationDirection() {
        return mTranslationDirection;
    }
}
