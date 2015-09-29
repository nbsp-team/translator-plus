package com.nbsp.translator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;

import com.nbsp.translator.models.theme.Theme;
import com.nbsp.translator.utils.PrefUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dimorinny on 27.09.15.
 */
public class ThemeManager {

    private static final int[] ATTRS = {R.attr.colorPrimary, R.attr.colorPrimaryDark};

    private static final int[] THEMES = {R.style.BlueTheme, R.style.RedTheme, R.style.IndigoTheme,
            R.style.PinkTheme, R.style.CyanTheme, R.style.GreenTheme, R.style.OrangeTheme,
            R.style.BrownTheme, R.style.BlueGreyTheme, R.style.GreyTheme, R.style.TealTheme,
            R.style.PurpleTheme};

    public static final int ERR_THEME_NOT_FOUND = -1;
    private static final int DEFAULT_THEME_INDEX = 0;

    private static ThemeManager ourInstance;
    private List<Theme> mThemes;
    private int mCurrentThemeIndex;

    private Context mContext;

    public static ThemeManager getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new ThemeManager(context);
        }
        return ourInstance;
    }

    private ThemeManager(Context context) {
        mContext = context.getApplicationContext();
        initThemes();
        initCurrentTheme();
    }

    private void initCurrentTheme() {
        int currentTheme = PrefUtils.getCurrentTheme(mContext);

        if (currentTheme == ERR_THEME_NOT_FOUND) {
            mCurrentThemeIndex = DEFAULT_THEME_INDEX;
        } else {
            mCurrentThemeIndex = currentTheme;
        }
    }

    @SuppressLint("Recycle")
    private void initThemes() {
        mThemes = new ArrayList<>();

        for (int theme : THEMES) {
            TypedArray ta = mContext.obtainStyledAttributes(theme, ATTRS);
            mThemes.add(new Theme(ta.getColor(0, Color.BLACK), ta.getColor(1, Color.WHITE)));
        }
    }

    public Theme getCurrentTheme() {
        return mThemes.get(mCurrentThemeIndex);
    }

    public int getCurrentThemeIndex() {
        return mCurrentThemeIndex;
    }

    public void setCurrentTheme(int theme) {
        mCurrentThemeIndex = theme;
        PrefUtils.setCurrentTheme(mContext, theme);
    }

    public List<Theme> getThemes() {
        return mThemes;
    }
}
