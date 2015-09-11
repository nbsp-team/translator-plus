package com.nbsp.translator.ui.translator.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Transition;
import android.widget.LinearLayout;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.nbsp.translator.R;
import com.nbsp.translator.api.Api;
import com.nbsp.translator.models.Translate;
import com.nbsp.translator.ui.translator.widget.TranslateResultBar;
import com.nbsp.translator.widget.EditTextBackEvent;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Dimorinny on 10.09.15.
 */

public class ActivityTranslator extends AppCompatActivity {

    @Bind(R.id.language_edit_text)
    protected EditTextBackEvent mLanguageEditText;

    @Bind(R.id.language_edit_text_container)
    protected LinearLayout mLanguageContainer;

    @Bind(R.id.language_result_container)
    protected LinearLayout mResultContainer;

    private TranslateResultBar mTranslateResultBar;
    private Subscription mTranslateSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translator);
        ButterKnife.bind(this);

        mTranslateResultBar = new TranslateResultBar(mResultContainer);
        setLanguageBarBackListener();
        disableBlinking();

        mTranslateSubscription = getTranslateSubscription();
    }

    private Subscription getTranslateSubscription() {
        return RxTextView.textChanges(mLanguageEditText)
                .skip(1)
                .debounce(350, TimeUnit.MILLISECONDS)
                .doOnNext(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                        if(charSequence.length() != 0) {
                            setResultBarStatusLoading();
                        }
                    }
                })
                .switchMap(new Func1<CharSequence, rx.Observable<Translate>>() {
                    @Override
                    public rx.Observable<Translate> call(CharSequence charSequence) {
                        return Api.getInstance().translate(charSequence.toString(), "en-ru");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Translate>() {
                    @Override
                    public void onCompleted() {

                    }

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
        mLanguageEditText.setOnEditTextImeBackListener(new EditTextBackEvent.EditTextImeBackListener() {
            @Override
            public void onImeBack(EditTextBackEvent ctrl, String text) {
                onBackPressed();
            }
        });
    }

    private void setResultBarStatusLoading() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTranslateResultBar.setWaitingStatus();
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

    @Override
    protected void onDestroy() {
        mTranslateSubscription.unsubscribe();
        super.onDestroy();
    }
}
