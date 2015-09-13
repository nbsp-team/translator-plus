package com.nbsp.translator.utils;

import android.content.Context;

import com.nbsp.translator.R;
import com.nbsp.translator.excpetion.network.AuthException;
import com.nbsp.translator.exception.network.ConnectionException;

/**
 * Created by Dimorinny on 11.09.15.
 */
public class ErrorConverter {
    public static String convertError(Context context, Throwable throwable) {
        String errorMessage = null;

        if (throwable instanceof AuthException) {
            errorMessage = context.getResources().getString(R.string.error_auth);
        } else if (throwable instanceof ConnectionException) {
            errorMessage = context.getResources().getString(R.string.error_network);
        }

        return errorMessage;
    }
}
