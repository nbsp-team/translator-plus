package com.nbsp.translator.data;

import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

import java.util.UUID;

/**
 * Created by nickolay on 26.09.15.
 */

@StorIOSQLiteType(table = "history")
public class HistoryItem {
    @StorIOSQLiteColumn(name = "id", key = true)
    String id;

    @StorIOSQLiteColumn(name = "date")
    long date;

    @StorIOSQLiteColumn(name = "original")
    String original;

    @StorIOSQLiteColumn(name = "translate")
    String translate;

    @StorIOSQLiteColumn(name = "lang")
    String lang;

    HistoryItem() {}

    public HistoryItem(String original, String translate, String lang) {
        this.date = System.currentTimeMillis();
        this.original = original;
        this.translate = translate;
        this.lang = lang;
        this.id = lang + original;
    }

    public String getOriginal() {
        return original;
    }

    public String getTranslate() {
        return translate;
    }

    public String getLang() {
        return lang;
    }
}