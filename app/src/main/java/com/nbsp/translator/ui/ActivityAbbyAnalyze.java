package com.nbsp.translator.ui;

import android.widget.ImageView;

import rx.Observable;

/**
 * Created by Dimorinny on 23.09.15.
 */
public class ActivityAbbyAnalyze extends ActivityImageAnalyze {

    @Override
    protected Observable<String> getCompleteObservable(String imagePath, ImageView imageView) {
        return null;
    }
}
