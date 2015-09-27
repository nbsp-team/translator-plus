package com.nbsp.translator.ui;

import android.support.v7.app.AppCompatActivity;

import com.nbsp.translator.BusProvider;
import com.nbsp.translator.event.ThemeChangeEvent;

/**
 * Created by Dimorinny on 27.09.15.
 */
public class BaseActivity extends AppCompatActivity {

    public void colorize(ThemeChangeEvent event) {}

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);

        // Initial colorizing
        BusProvider.getInstance().post(new ThemeChangeEvent());
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }
}
