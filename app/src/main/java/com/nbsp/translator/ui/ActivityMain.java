package com.nbsp.translator.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.nbsp.translator.R;
import com.nbsp.translator.models.TranslationDirection;
import com.nbsp.translator.ui.fragment.FragmentLanguagePicker;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;

public class ActivityMain extends AppCompatActivity implements FragmentLanguagePicker.OnLanguagePickerEventsListener {

    @Bind(R.id.language_input_container)
    protected LinearLayout mLanguageInput;

    @Bind(R.id.languages_bar)
    protected LinearLayout mLanguagesBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @SuppressWarnings("unchecked")
    @OnClick(R.id.language_input_container)
    protected void startTranslatorWithAnimation() {
        Intent intent = new Intent(getApplicationContext(), ActivityTranslator.class);

        Pair<View, String> p1 = Pair.create((View) mLanguageInput, mLanguageInput.getTransitionName());
        Pair<View, String> p2 = Pair.create(mLanguagesBar, mLanguagesBar.getTransitionName());

        ActivityOptionsCompat activityOptions = ActivityOptionsCompat
                .makeSceneTransitionAnimation(ActivityMain.this, p1, p2);

        ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
    }

    @Override
    public void onCreateChangeObservable(Observable<TranslationDirection> observable) {
        observable.subscribe(new Subscriber<TranslationDirection>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(TranslationDirection translationDirection) {
                Log.d("ActivityTranslator", "newdirection");
            }
        });
    }

    @OnClick(R.id.main_bottom_button_image)
    protected void captureImage() {
        Intent i = new Intent(this, ActivityImageAnalyze.class);
        startActivity(i);
    }
}
