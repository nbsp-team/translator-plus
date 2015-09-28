package com.nbsp.translator.ui;

import android.widget.ImageView;

import com.nbsp.translator.App;
import com.nbsp.translator.ThemeManager;
import com.nbsp.translator.api.ApiCloudSight;
import com.nbsp.translator.event.ThemeChangeEvent;
import com.nbsp.translator.models.cloudsight.CSCheckResultResponse;
import com.nbsp.translator.models.theme.Theme;
import com.squareup.otto.Subscribe;

import java.io.File;

import retrofit.mime.TypedFile;
import rx.Observable;

/**
 * Created by Dimorinny on 23.09.15.
 */
public class ActivityCloudSightAnalyze extends ActivityImageAnalyze {

    @Subscribe
    @Override
    public void colorize(ThemeChangeEvent event) {
        Theme currentTheme = ThemeManager.getInstance(getApplicationContext()).getCurrentTheme();
        mAnalyzeBar.setBackgroundColor(currentTheme.getPrimaryColor());
    }

    @Override
    protected Observable<String> analyzeImage(String imagePath, ImageView imageView) {
        TypedFile imageFile = new TypedFile("image/jpeg", new File(imagePath));

        return ApiCloudSight.getInstance().recognize(imageFile,
                App.getInstance().getTranslationDirection().getFrom().getLanguageCode())
                .map(csSendImageResponse -> {
                    CSCheckResultResponse checkResultResponse;
                    while (true) {
                        checkResultResponse = ApiCloudSight.getInstance().checkResponse(csSendImageResponse.getToken());

                        if (!checkResultResponse.getStatus().equals(CSCheckResultResponse.STATUS_NOT_COMPLETED)) {
                            break;
                        }

                        try {
                            Thread.sleep(3 * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    return checkResultResponse;
                })
                .map(CSCheckResultResponse::getName);
    }
}
