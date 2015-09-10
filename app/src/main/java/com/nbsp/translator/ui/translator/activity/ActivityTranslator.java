package com.nbsp.translator.ui.translator.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Transition;

import com.nbsp.translator.R;
import com.nbsp.translator.widget.EditTextBackEvent;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dimorinny on 10.09.15.
 */

public class ActivityTranslator extends AppCompatActivity {

    @Bind(R.id.language_edit_text)
    protected EditTextBackEvent mLanguageEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translator);
        ButterKnife.bind(this);

        setLanguageBarBackListener();
        disableBlinking();
    }

    private void setLanguageBarBackListener() {
        mLanguageEditText.setOnEditTextImeBackListener(new EditTextBackEvent.EditTextImeBackListener() {
            @Override
            public void onImeBack(EditTextBackEvent ctrl, String text) {
                onBackPressed();
            }
        });
    }

    private void disableBlinking() {
        Transition fade = new Fade();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setExitTransition(fade);
        getWindow().setEnterTransition(fade);
    }
}
