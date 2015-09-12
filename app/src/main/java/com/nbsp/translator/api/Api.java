package com.nbsp.translator.api;

import com.nbsp.translator.api.request.YandexTranslator;
import com.nbsp.translator.excpetion.network.AuthException;
import com.nbsp.translator.excpetion.network.ConnectionException;
import com.nbsp.translator.models.Translate;

import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import rx.Observable;



/**
 * Created by Dimorinny on 10.09.15.
 */

public class Api {
    private static Api instance;

    private Api() {
        initRestAdapter();
        initRequests();
    }

    public static Api getInstance() {
        if (instance == null) {
            instance = new Api();
        }

        return instance;
    }

    private static String BASE_URL = "https://translate.yandex.net/api/v1.5/tr.json";
    private static String API_KEY = "trnsl.1.1.20150910T200437Z.e7c6c0d008f6e66e.46d4507dc332bb75eacc9414a76000251048c3be";

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

    public Observable<Translate> translate(String text, String lang) {
        return mYandexTranslator.translate(text, lang);
    }
}
