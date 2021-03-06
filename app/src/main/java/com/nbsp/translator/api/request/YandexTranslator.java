package com.nbsp.translator.api.request;

import com.nbsp.translator.models.Language;
import com.nbsp.translator.models.TranslationDirection;
import com.nbsp.translator.models.yandextranslator.TranslateResult;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by Dimorinny on 10.09.15.
 */

public interface YandexTranslator {

    @GET("/translate")
    Observable<TranslateResult> translate(@Query("text") String text, @Query("lang") TranslationDirection lang, @Query("options") int options);

    @GET("/getLangs")
    Observable<List<Language>> getLanguages(@Query("ui") String lang);
}
