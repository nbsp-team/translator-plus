package com.nbsp.translator.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nbsp.translator.ui.ActivityTranslator;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;
import com.pushtorefresh.storio.sqlite.queries.Query;

import java.util.List;

import rx.Observable;

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

    public static Observable<List<HistoryItem>> getLastItems(Context context, int size) {
        return getStorIO(context)
                .get()
                .listOfObjects(HistoryItem.class)
                .withQuery(Query.builder()
                        .table("history")
                        .limit(size)
                        .build())
                .prepare()
                .createObservable();
    }

    public static void putObject(Context context, HistoryItem historyItem) {
        getStorIO(context)
                .put()
                .object(historyItem)
                .prepare()
                .executeAsBlocking();
    }

    private static class OpenHelper extends SQLiteOpenHelper {
        public OpenHelper(Context context) {
            super(context, DB_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table history ("
                    + "id text primary key,"
                    + "original text,"
                    + "translate text,"
                    + "lang text"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
