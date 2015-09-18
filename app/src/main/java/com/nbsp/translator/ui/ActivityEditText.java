package com.nbsp.translator.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Transition;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.nbsp.translator.App;
import com.nbsp.translator.R;
import com.nbsp.translator.api.Api;
import com.nbsp.translator.models.TranslateResult;
import com.nbsp.translator.models.TranslationTask;
import com.nbsp.translator.ui.widget.TranslateResultBar;
import com.nbsp.translator.ui.widget.EditTextBackEvent;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Dimorinny on 10.09.15.
 */

public class ActivityEditText extends AppCompatActivity {
    public static final String ORIGINAL_TEXT_EXTRA = "text";

    @Bind(R.id.language_edit_text)
    protected EditTextBackEvent mOriginalEditText;

    @Bind(R.id.language_edit_text_container)
    protected LinearLayout mLanguageContainer;

    @Bind(R.id.language_result_container)
    protected LinearLayout mResultContainer;

    @Bind(R.id.close_button)
    protected ImageView mClearButton;

    private TranslateResultBar mTranslateResultBar;
    private Subscription mTranslateSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text);
        ButterKnife.bind(this);

        if (getIntent() != null) {
            String text = getIntent().getExtras().getString(ORIGINAL_TEXT_EXTRA);
            mOriginalEditText.setText(text);
        }

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

    protected void setTextResult() {
        Intent intent = new Intent();
        intent.putExtra(ActivityTranslator.ORIGINAL_TEXT_EXTRA, mOriginalEditText.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    private Subscription getTranslateSubscription() {
        return RxTextView.textChanges(mOriginalEditText)
                .skip(1)
                .doOnNext(charSequence -> {
                    if (charSequence.length() != 0) {
                        ActivityEditText.this.setResultBarStatusLoading();
                    }
                })
                .debounce(350, TimeUnit.MILLISECONDS)
                .switchMap(charSequence -> Api.getInstance().translate(new TranslationTask(charSequence.toString(), App.getInstance().getTranslationDirection())))
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
        mOriginalEditText.setHint(App.getInstance().getTranslationDirection().getFrom().getName());
        mTranslateResultBar.setHint(App.getInstance().getTranslationDirection().getTo().getName());
    }

    private void setLanguageBarBackListener() {
        mOriginalEditText.setOnEditTextImeBackListener((ctrl, text) -> onBackPressed());
    }

    private void setResultBarClickListener() {
        mTranslateResultBar.setOnCLickListener(view -> {
            if (mTranslateResultBar.getCurrentResult().length() != 0) {
                setTextResult();
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
