package com.nbsp.translator.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.nbsp.translator.R;
import com.nbsp.translator.api.Api;
import com.nbsp.translator.api.Languages;
import com.nbsp.translator.models.TranslateResult;
import com.nbsp.translator.models.TranslationDirection;
import com.nbsp.translator.ui.fragment.FragmentLanguagePicker;
import com.nbsp.translator.ui.widget.TranslateResultBar;
import com.nbsp.translator.widget.EditTextBackEvent;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Dimorinny on 10.09.15.
 */

public class ActivityTranslator extends AppCompatActivity implements FragmentLanguagePicker.OnLanguagePickerEventsListener {

    private final int REQUEST_CODE_RESULT_CHANGED = 228;

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
        initHints();

        mTranslateSubscription = getTranslateSubscription();
    }

    @OnClick(R.id.close_button)
    protected void onCloseClicked(View view) {
        onBackPressed();
    }

    @SuppressWarnings("unchecked")
    protected void startResultActivityWithAnimation() {

        Intent intent = new Intent(getApplicationContext(), ActivityResult.class);
        intent.putExtra(ActivityResult.ARG_RESULT_FROM, mLanguageEditText.getText().toString());
        intent.putExtra(ActivityResult.ARG_RESULT_TO, mTranslateResultBar.getCurrentResult());

//        Pair<View, String> p1 = Pair.create((View) mLanguagesBar, mLanguagesBar.getTransitionName());
        Pair<View, String> p2 = Pair.create((View) mLanguageContainer, mLanguageContainer.getTransitionName());

        ActivityOptionsCompat activityOptions = ActivityOptionsCompat
                .makeSceneTransitionAnimation(ActivityTranslator.this, p2);

        ActivityCompat.startActivityForResult(this, intent, REQUEST_CODE_RESULT_CHANGED, activityOptions.toBundle());
    }


    private Subscription getTranslateSubscription() {
        return RxTextView.textChanges(mLanguageEditText)
                .skip(1)
                .doOnNext(charSequence -> {
                    if (charSequence.length() != 0) {
                        ActivityTranslator.this.setResultBarStatusLoading();
                    }
                })
                .debounce(350, TimeUnit.MILLISECONDS)
                .switchMap(charSequence -> Api.getInstance().translate(charSequence.toString(), Languages.getInstance().getTranslationDirection()))
                .observeOn(AndroidSchedulers.mainThread())
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
                        mTranslateResultBar.setCurrentResult(translateResult.getTexts().get(0));
                    }
                });
    }

    private void initHints() {
        mLanguageEditText.setHint(Languages.getInstance().getTranslationDirection().getFrom().getName());
        mTranslateResultBar.setHint(Languages.getInstance().getTranslationDirection().getTo().getName());
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

    @Override
    public void onCreateChangeObservable(Observable<TranslationDirection> observable) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_RESULT_CHANGED:
                    String newResult = data.getStringExtra(ActivityResult.ARG_RESULT_CHANGED);
                    if (newResult != null) {
                        mTranslateResultBar.setCurrentResult(newResult);
                    }
                    break;
            }
        }
    }
}
