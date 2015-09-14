package com.nbsp.translator.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Transition;
import android.widget.TextView;

import com.nbsp.translator.R;
import com.nbsp.translator.api.Languages;
import com.nbsp.translator.models.TranslationDirection;
import com.nbsp.translator.ui.fragment.FragmentLanguagePicker;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;

public class ActivityResult extends AppCompatActivity implements FragmentLanguagePicker.OnLanguagePickerEventsListener {

    public static final String ARG_RESULT_FROM = "arg_result_from";
    public static final String ARG_RESULT_TO = "arg_result_to";

    @Bind(R.id.result_language_from)
    protected TextView mLanguageFrom;

    @Bind(R.id.result_language_to)
    protected TextView mLanguageTo;

    @Bind(R.id.result_from)
    protected TextView mResultFrom;

    @Bind(R.id.result_to)
    protected TextView mResultTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);

        disableBlinking();
        setResults();
        setLanguages();
    }

    private void setLanguages() {
        mLanguageFrom.setText(Languages.getInstance().getTranslationDirection().getFrom().getName().toUpperCase());
        mLanguageTo.setText(Languages.getInstance().getTranslationDirection().getTo().getName().toUpperCase());
    }

    private void setResults() {
        Bundle extras = getIntent().getExtras();

        String resultFrom = extras.getString(ARG_RESULT_FROM);
        String resultTo = extras.getString(ARG_RESULT_TO);

        if (resultFrom != null && resultTo != null) {
            mResultFrom.setText(resultFrom);
            mResultTo.setText(resultTo);
        }
    }

    private void disableBlinking() {
        Transition fade = new Fade();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setExitTransition(fade);
        getWindow().setEnterTransition(fade);
    }

    @Override
    public void onCreateChangeObservable(Observable<TranslationDirection> observable) {

    }
}
