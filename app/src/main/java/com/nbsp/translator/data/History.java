package com.nbsp.translator.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;

/**
 * Created by nickolay on 26.09.15.
 */

public class History {
    public static final String DB_NAME = "history.db";
    private static StorIOSQLite mStorIOSQLite;

    public static StorIOSQLite getStorIO(Context context) {
        if (mStorIOSQLite != null) {
            return mStorIOSQLite;
        }

        mStorIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(new OpenHelper(context))
                .addTypeMapping(HistoryItem.class, SQLiteTypeMapping.<HistoryItem>builder()
                        .putResolver(new HistoryItemStorIOSQLitePutResolver())
                        .getResolver(new HistoryItemStorIOSQLiteGetResolver())
                        .deleteResolver(new HistoryItemStorIOSQLiteDeleteResolver())
                        .build())
        .build();

        return mStorIOSQLite;
    }

    private static class OpenHelper extends SQLiteOpenHelper {
        public OpenHelper(Context context) {
            super(context, DB_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
