package com.nbsp.translator.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by Dimorinny on 12.03.15.
 */

public class ResponsiveScrollView extends ScrollView {

    public interface OnScrollListener {
        public void onScroll(int x, int y, int oldX, int oldY);
    }

    private OnScrollListener mOnScrollListener;

    public ResponsiveScrollView(Context context) {
        this(context, null, 0);
    }

    public ResponsiveScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ResponsiveScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldX, int oldY) {
        super.onScrollChanged(x, y, oldX, oldY);

        if(mOnScrollListener != null) {
            mOnScrollListener.onScroll(x, y, oldX, oldY);
        }
    }

    public OnScrollListener getOnScrollListener() {
        return mOnScrollListener;
    }

    public void setOnScrollListener(OnScrollListener mOnEndScrollListener) {
        this.mOnScrollListener = mOnEndScrollListener;
    }

    private boolean mScrollable = true;

    public boolean isScrollable() {
        return mScrollable;
    }

    public void setScrollable(boolean scrollable) {
        this.mScrollable = scrollable;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mScrollable && super.onInterceptTouchEvent(ev);
    }
}

