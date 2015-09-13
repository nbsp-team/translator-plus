package com.nbsp.translator.ui.result.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Transition;

import com.nbsp.translator.R;
import com.nbsp.translator.models.TranslationDirection;
import com.nbsp.translator.ui.main.activity.LanguagePicker;

import butterknife.ButterKnife;

public class ActivityResult extends AppCompatActivity implements LanguagePicker.OnLanguagePickerEventsListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);

        disableBlinking();
    }

    private void disableBlinking() {
        Transition fade = new Fade();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setExitTransition(fade);
        getWindow().setEnterTransition(fade);
    }

    @Override
    public void onTranslationDirectionChanged(TranslationDirection direction) {

    }
}
