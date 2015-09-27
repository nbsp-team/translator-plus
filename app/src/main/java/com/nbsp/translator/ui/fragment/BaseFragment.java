package com.nbsp.translator.ui.fragment;

import android.app.Fragment;

import com.nbsp.translator.BusProvider;
import com.nbsp.translator.event.ThemeChangeEvent;

/**
 * Created by Dimorinny on 27.09.15.
 */
public class BaseFragment extends Fragment {

    public void colorize(ThemeChangeEvent event) {}

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);

        // Initial colorizing
        BusProvider.getInstance().post(new ThemeChangeEvent());
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }
}
