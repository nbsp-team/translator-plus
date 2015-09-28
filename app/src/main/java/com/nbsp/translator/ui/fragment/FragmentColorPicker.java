package com.nbsp.translator.ui.fragment;

import android.app.Fragment;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;

import com.nbsp.translator.BusProvider;
import com.nbsp.translator.R;
import com.nbsp.translator.ThemeManager;
import com.nbsp.translator.event.ThemeChangeEvent;
import com.nbsp.translator.models.theme.Theme;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dimorinny on 27.09.15.
 */
public class FragmentColorPicker extends Fragment {

    @Bind(R.id.colors_container)
    protected GridLayout mColorsContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_color_picker, container, false);
        ButterKnife.bind(this, view);

        initColors();

        return view;
    }

    private void initColors() {
        List<Theme> themeList = ThemeManager.getInstance(getActivity()).getThemes();

        for (int i = 0, themeListSize = themeList.size(); i < themeListSize; i++) {
            Theme currentTheme = themeList.get(i);

            LayoutInflater inflater = getActivity().getLayoutInflater();
            FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.item_color_pick, mColorsContainer, false);

            initColorPickerItem(layout, currentTheme, i);
            mColorsContainer.addView(layout);
        }

        setSelectedItem(true, ThemeManager.getInstance(getActivity()).getCurrentThemeIndex());
    }

    private void initColorPickerItem(FrameLayout layout, Theme theme, final int index) {
        View item = layout.findViewById(R.id.color_picker);
        ((GradientDrawable)item.getBackground()).setColor(theme.getPrimaryColor());

        layout.setOnClickListener(v -> {
            setSelectedItem(false, ThemeManager.getInstance(getActivity()).getCurrentThemeIndex());
            ThemeManager.getInstance(getActivity()).setCurrentTheme(index);
            setSelectedItem(true, ThemeManager.getInstance(getActivity()).getCurrentThemeIndex());
            BusProvider.getInstance().post(new ThemeChangeEvent());
        });
    }

    private void setSelectedItem(boolean selected, int index) {
        mColorsContainer.getChildAt(index)
                .setSelected(selected);
    }
}
