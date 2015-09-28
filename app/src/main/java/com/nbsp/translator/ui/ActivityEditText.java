package com.nbsp.translator.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Transition;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.nbsp.translator.App;
import com.nbsp.translator.R;
import com.nbsp.translator.ThemeManager;
import com.nbsp.translator.api.ApiTranslator;
import com.nbsp.translator.event.ThemeChangeEvent;
import com.nbsp.translator.models.TranslationTask;
import com.nbsp.translator.models.theme.Theme;
import com.nbsp.translator.models.yandextranslator.TranslateResult;
import com.nbsp.translator.ui.widget.EditTextBackEvent;
import com.nbsp.translator.ui.widget.TranslateResultBar;
import com.squareup.otto.Subscribe;

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

public class ActivityEditText extends BaseActivity {
    public static final String ORIGINAL_TEXT_EXTRA = "text";

    @Bind(R.id.language_edit_text)
    protected EditTextBackEvent mOriginalEditText;

    @Bind(R.id.language_edit_text_container)
    protected LinearLayout mLanguageContainer;

    @Bind(R.id.language_result_container)
    protected LinearLayout mResultContainer;

    @Bind(R.id.close_button)
    protected ImageView mClearButton;

    @Bind(R.id.show_translation)
    protected ImageView mShowTranslationIcon;

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
            mOriginalEditText.setSelection(mOriginalEditText.getText().length());
        }

        mTranslateResultBar = new TranslateResultBar(mResultContainer);

        setResultListeners();
        setLanguageBarBackListener();
        disableBlinking();
        initHints();

        mTranslateSubscription = getTranslateSubscription();
    }

    @Subscribe
    @Override
    public void colorize(ThemeChangeEvent event) {
        Theme currentTheme = ThemeManager.getInstance(getApplicationContext()).getCurrentTheme();
        getWindow().setStatusBarColor(currentTheme.getPrimaryDarkColor());
        mShowTranslationIcon.setColorFilter(currentTheme.getPrimaryColor());
    }

    @OnClick(R.id.close_button)
    protected void onCloseClicked(View view) {
        mOriginalEditText.setText("");
        onBackPressed();
    }

    private Subscription getTranslateSubscription() {
        return RxTextView.textChanges(mOriginalEditText)
                .doOnNext(charSequence -> {
                    if (charSequence.length() == 0) {
                        mTranslateResultBar.setCurrentResult("");
                    }
                })
                .filter(charSequence -> charSequence.length() != 0)
                .doOnNext(charSequence -> setResultBarStatusLoading())
                .debounce(350, TimeUnit.MILLISECONDS)
                .switchMap(charSequence -> ApiTranslator.getInstance().translate(new TranslationTask(charSequence.toString(), App.getInstance().getTranslationDirection())))
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

    private void setResultListeners() {
        mTranslateResultBar.setOnCLickListener(view -> {
            onResult();
        });

        mOriginalEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onResult();
                return true;
            }
            return false;
        });
    }

    private void onResult() {
        if (mTranslateResultBar.getCurrentResult().length() != 0) {
            onBackPressed();
        }
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


    // Юзаем onBackPressed, потому что с finish() ломается анимация
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(ActivityTranslator.ORIGINAL_TEXT_EXTRA, mOriginalEditText.getText().toString());
        setResult(RESULT_OK, intent);

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mOriginalEditText.getWindowToken(), 0);

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        mTranslateSubscription.unsubscribe();
        super.onDestroy();
    }
}
