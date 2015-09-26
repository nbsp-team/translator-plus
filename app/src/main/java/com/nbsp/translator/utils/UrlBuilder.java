package com.nbsp.translator.utils;

import android.net.Uri;

/**
 * Created by Dimorinny on 25.09.15.
 */
public class UrlBuilder {

    public static Uri getGoogleTranslateTtlUri(String text, String language) {

        return new Uri.Builder()
                .scheme("https")
                .authority("translate.google.com")
                .path("translate_tts")
                .appendQueryParameter("ie", "UTF-8")
                .appendQueryParameter("client", "t")
                .appendQueryParameter("q", text)
                .appendQueryParameter("tl", language)
                .build();
    }
}
