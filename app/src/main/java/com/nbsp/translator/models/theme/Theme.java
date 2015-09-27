package com.nbsp.translator.models.theme;

/**
 * Created by Dimorinny on 27.09.15.
 */
public class Theme {

    private int mPrimaryColor;
    private int mPrimaryDarkColor;

    private boolean mIsSelected;
    private boolean mIsCurrentTheme;

    public Theme(int mPrimaryColor, int mPrimaryDarkColor) {
        this.mPrimaryColor = mPrimaryColor;
        this.mPrimaryDarkColor = mPrimaryDarkColor;
        mIsSelected = false;
        mIsCurrentTheme = false;
    }

    public int getPrimaryColor() {
        return mPrimaryColor;
    }

    public void setPrimaryColor(int mPrimaryColor) {
        this.mPrimaryColor = mPrimaryColor;
    }

    public int getPrimaryDarkColor() {
        return mPrimaryDarkColor;
    }

    public void setPrimaryDarkColor(int mPrimaryDarkColor) {
        this.mPrimaryDarkColor = mPrimaryDarkColor;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public void setSelected(boolean mIsSelected) {
        this.mIsSelected = mIsSelected;
    }

    public boolean isCurrentTheme() {
        return mIsCurrentTheme;
    }

    public void setCurrentTheme(boolean mIsCurrentTheme) {
        this.mIsCurrentTheme = mIsCurrentTheme;
    }
}
