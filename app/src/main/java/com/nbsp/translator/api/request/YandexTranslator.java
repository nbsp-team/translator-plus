package com.nbsp.translator.api.request;

import com.nbsp.translator.models.TranslateResult;
import com.nbsp.translator.models.TranslationDirection;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by Dimorinny on 10.09.15.
 */
public interface YandexTranslator {

    @GET("/translate")
    Observable<TranslateResult> translate(@Query("text") String text, @Query("lang") TranslationDirection lang);
}
