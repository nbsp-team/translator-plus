package com.nbsp.translator.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.transition.Fade;
import android.transition.Transition;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nbsp.translator.R;
import com.nbsp.translator.api.Api;
import com.nbsp.translator.api.Languages;
import com.nbsp.translator.models.TranslateResult;
import com.nbsp.translator.models.TranslationDirection;
import com.nbsp.translator.ui.fragment.FragmentLanguagePicker;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class ActivityResult extends AppCompatActivity implements FragmentLanguagePicker.OnLanguagePickerEventsListener {

    public static final String ARG_RESULT_FROM = "arg_result_from";
    public static final String ARG_RESULT_TO = "arg_result_to";
    public static final String ARG_RESULT_CHANGED = "arg_result_changed";

    @Bind(R.id.result_language_from)
    protected TextView mLanguageFrom;

    @Bind(R.id.result_language_to)
    protected TextView mLanguageTo;

    @Bind(R.id.result_from)
    protected TextView mResultFrom;

    @Bind(R.id.result_to)
    protected TextView mResultTo;

    @Bind(R.id.loading_progress_bar)
    protected ProgressBar mLoadingProgressBar;

    @Bind(R.id.container_language_from)
    protected LinearLayout mContainerLanguageFrom;

    @Bind(R.id.container_language_to)
    protected CardView mContainerLanguageTo;

    @Bind(R.id.languages_bar)
    protected LinearLayout mLanguagesBar;

    private Subscription mLanguageChangedSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);

        disableBlinking();
        setResults();
        setLanguages();
    }

    @OnClick(R.id.container_language_from)
    protected void containerLanguageFromClickListener(View view) {
        onBackPressed();
    }

    @OnClick(R.id.close_button)
    protected void closeButtonClickListener() {
        Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
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
        mLanguageChangedSubscription = observable
                .doOnNext(translationDirection -> hideResultCard())
                .switchMap(translationDirection -> Api.getInstance().translate(mResultFrom.getText().toString(), translationDirection))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(translateResult -> {
                    TranslationDirection translationDirection = Languages.getInstance().getTranslationDirection();
                    setContainerFromData(translationDirection.getFrom().getName());
                    setContainerToData(translationDirection.getTo().getName(), translateResult.getTexts().get(0));
                })
                .doOnNext(translateResult -> showResultCard())
                .subscribe(new Observer<TranslateResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(TranslateResult translateResult) {

                    }
                });
    }

    private void setContainerFromData(String languageName) {
        mLanguageFrom.setText(languageName.toUpperCase());
    }

    private void setContainerToData(String languageName, String result) {
        mLanguageTo.setText(languageName.toUpperCase());
        mResultTo.setText(result);
    }

    private void hideResultCard() {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.result_card_fade_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mContainerLanguageTo.setVisibility(View.GONE);
                mLoadingProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        runOnUiThread(() -> mContainerLanguageTo.startAnimation(animation));
    }

    private void showResultCard() {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.result_card_fade_in);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mLoadingProgressBar.setVisibility(View.GONE);
                mContainerLanguageTo.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        runOnUiThread(() -> mContainerLanguageTo.startAnimation(animation));
    }

    @Override
    protected void onDestroy() {
        if (mLanguageChangedSubscription != null) {
            mLanguageChangedSubscription.unsubscribe();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        Bundle bundle = new Bundle();
        bundle.putString(ARG_RESULT_CHANGED, mResultTo.getText().toString());

        Intent mIntent = new Intent();
        mIntent.putExtras(bundle);

        setResult(RESULT_OK, mIntent);

        super.onBackPressed();
    }
}
