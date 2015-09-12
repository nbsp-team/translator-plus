package com.nbsp.translator.ui.translator.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Transition;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.nbsp.translator.R;
import com.nbsp.translator.api.Api;
import com.nbsp.translator.models.Translate;
import com.nbsp.translator.ui.result.activity.ActivityResult;
import com.nbsp.translator.ui.translator.widget.TranslateResultBar;
import com.nbsp.translator.widget.EditTextBackEvent;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Dimorinny on 10.09.15.
 */

public class ActivityTranslator extends AppCompatActivity {

    @Bind(R.id.languages_bar)
    protected LinearLayout mLanguagesBar;

    @Bind(R.id.language_edit_text)
    protected EditTextBackEvent mLanguageEditText;

    @Bind(R.id.language_edit_text_container)
    protected LinearLayout mLanguageContainer;

    @Bind(R.id.language_result_container)
    protected LinearLayout mResultContainer;

    @Bind(R.id.close_button)
    protected ImageView mCloseButton;

    private TranslateResultBar mTranslateResultBar;
    private Subscription mTranslateSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translator);
        ButterKnife.bind(this);

        mTranslateResultBar = new TranslateResultBar(mResultContainer);

        setResultBarClickListener();
        setLanguageBarBackListener();
        disableBlinking();

        mTranslateSubscription = getTranslateSubscription();
    }

    @OnClick(R.id.close_button)
    protected void onCloseClicked(View view) {
        onBackPressed();
    }

    @SuppressWarnings("unchecked")
    protected void startResultActivityWithAnimation() {

        Intent intent = new Intent(getApplicationContext(), ActivityResult.class);

        Pair<View, String> p1 = Pair.create((View) mLanguagesBar, mLanguagesBar.getTransitionName());
        Pair<View, String> p2 = Pair.create((View) mLanguageContainer, mLanguageContainer.getTransitionName());

        ActivityOptionsCompat activityOptions = ActivityOptionsCompat
                .makeSceneTransitionAnimation(ActivityTranslator.this, p1, p2);

        ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
    }


    private Subscription getTranslateSubscription() {
        return RxTextView.textChanges(mLanguageEditText)
                .skip(1)
                .doOnNext(charSequence -> {
                    if (charSequence.length() != 0) {
                        setResultBarStatusLoading();
                    }
                })
                .debounce(350, TimeUnit.MILLISECONDS)
                .switchMap(charSequence -> Api.getInstance().translate(charSequence.toString(), "en-ru"))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Translate>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Translate translate) {
                        mTranslateResultBar.setCurrentResult(translate.getTexts().get(0));
                    }
                });
    }

    private void setLanguageBarBackListener() {
        mLanguageEditText.setOnEditTextImeBackListener((ctrl, text) -> onBackPressed());
    }

    private void setResultBarClickListener() {
        mTranslateResultBar.setOnCLickListener(view -> {
            if (mTranslateResultBar.getCurrentResult().length() != 0) {
                startResultActivityWithAnimation();
            }
        });
    }

    private void setResultBarStatusLoading() {
        runOnUiThread(mTranslateResultBar::setWaitingStatus);
    }

    private void disableBlinking() {
        Transition fade = new Fade();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setExitTransition(fade);
        getWindow().setEnterTransition(fade);
    }

    @Override
    protected void onDestroy() {
        mTranslateSubscription.unsubscribe();
        super.onDestroy();
    }
}
