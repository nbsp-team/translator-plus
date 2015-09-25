package com.nbsp.translator.api;

import com.nbsp.translator.api.request.YandexTranslator;
import com.nbsp.translator.exception.network.AuthException;
import com.nbsp.translator.exception.network.ConnectionException;
import com.nbsp.translator.models.yandextranslator.Language;
import com.nbsp.translator.models.yandextranslator.TranslateResult;
import com.nbsp.translator.models.TranslationTask;

import java.util.List;

import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import rx.Observable;


/**
 * Created by Dimorinny on 10.09.15.
 */

public class ApiTranslator {
    private static ApiTranslator instance;

    private ApiTranslator() {
        initRestAdapter();
        initRequests();
    }

    public static ApiTranslator getInstance() {
        if (instance == null) {
            instance = new ApiTranslator();
        }

        return instance;
    }

    private final String BASE_URL = "https://translate.yandex.net/api/v1.5/tr.json";
    private final String API_KEY = "trnsl.1.1.20150910T200437Z.e7c6c0d008f6e66e.46d4507dc332bb75eacc9414a76000251048c3be";

    private RestAdapter mRestAdapter;
    private YandexTranslator mYandexTranslator;

    private void initRestAdapter() {
        mRestAdapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL)
                .setRequestInterceptor(request -> request.addQueryParam("key", API_KEY))
                .setErrorHandler(cause -> {
                    switch(cause.getKind()) {
                        case NETWORK:
                            return new ConnectionException();
                        case HTTP:
                            return new AuthException();
                        default:
                            return new RuntimeException();
                    }
                })
                .setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("RETROFIT"))
                .build();
    }

    private void initRequests() {
        mYandexTranslator = mRestAdapter.create(YandexTranslator.class);
    }

    public Observable<TranslateResult> translate(TranslationTask task) {
        return mYandexTranslator.translate(task.getTextToTranslate(), task.getTranslationDirection());
    }

    public Observable<List<Language>> getLanguages(String lang) {
        return mYandexTranslator.getLanguages(lang);
    }
}
