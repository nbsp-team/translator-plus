package com.nbsp.translator.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.nbsp.translator.App;
import com.nbsp.translator.R;
import com.nbsp.translator.api.ApiTranslator;
import com.nbsp.translator.data.History;
import com.nbsp.translator.data.HistoryItem;
import com.nbsp.translator.models.TranslationDirection;
import com.nbsp.translator.models.TranslationTask;
import com.nbsp.translator.models.yandextranslator.TranslateResult;
import com.nbsp.translator.ui.fragment.FragmentHistory;
import com.nbsp.translator.ui.fragment.FragmentLanguagePicker;
import com.nbsp.translator.ui.fragment.FragmentTranslationCard;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class ActivityTranslator extends AppCompatActivity implements FragmentHistory.OnHistoryItemSelectedListener {
    public static final String ORIGINAL_TEXT_EXTRA = "text";
    private static final int EDIT_TEXT_ACTIVITY_REQUEST_CODE = 0;
    private static final int ANALYZE_PHOTO_ACTIVITY_REQUEST_CODE = 1;

    @Bind(R.id.toolbar)
    protected View mToolbar;

    @Bind(R.id.original_text_input_container)
    protected LinearLayout mLanguageInputContainer;

    @Bind(R.id.original_text_input)
    protected EditText mOriginalTextInput;

    @Bind(R.id.loading_progress_bar)
    protected ProgressBar mLoadingProgressBar;

    @Bind(R.id.result_container)
    protected View mResultContainer;

    @Bind(R.id.scroll)
    protected ScrollView mScrollView;

    private FragmentLanguagePicker mLanguagePicker;
    private Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translator);
        ButterKnife.bind(this);

        Observable<String> originalTextObservable = RxTextView.textChanges(mOriginalTextInput).map(CharSequence::toString);
        mLanguagePicker = (FragmentLanguagePicker) getFragmentManager().findFragmentById(R.id.language_picker);
        Observable<TranslationDirection> languageDirectionObservable = mLanguagePicker.getObservable();

        Observable<TranslateResult> resultObservable = Observable.combineLatest(
                languageDirectionObservable,
                originalTextObservable,
                (direction, text) -> new TranslationTask(text, direction)
        )
                .debounce(350, TimeUnit.MILLISECONDS)
                .doOnNext(translationDirection -> setProgress(true))
                .switchMap(task -> ApiTranslator.getInstance().translate(task))
                .doOnNext(result -> setProgress(false));

        FragmentTranslationCard translationCard = (FragmentTranslationCard) getFragmentManager().findFragmentById(R.id.translation_result_card);
        Subscription translationCardSubscription = translationCard.subscribe(resultObservable);

        Subscription historySubscription = resultObservable
                .debounce(600, TimeUnit.MILLISECONDS)
                .map(result -> new HistoryItem(
                        mOriginalTextInput.getText().toString(),
                        result.getText(),
                        result.getLang()
                ))
                .filter(item -> item.getOriginal().length() > 0 && item.getTranslate().length() > 0)
                .subscribe(historyItem -> {
                    History.putObject(ActivityTranslator.this, historyItem);
                });

        mSubscription = new CompositeSubscription(translationCardSubscription, historySubscription);
    }

    private void setProgress(boolean progress) {
        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        runOnUiThread(() -> {
            if (progress) {
                mLoadingProgressBar.startAnimation(fadeIn);
                mResultContainer.startAnimation(fadeOut);
            } else {
                mLoadingProgressBar.startAnimation(fadeOut);
                mResultContainer.startAnimation(fadeIn);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mOriginalTextInput.setSelection(mOriginalTextInput.getText().length());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscription.unsubscribe();
    }

    @OnClick(R.id.original_text_input)
    protected void openEditTextWithAnimation() {
        Intent intent = new Intent(getApplicationContext(), ActivityEditText.class);
        intent.putExtra(ActivityEditText.ORIGINAL_TEXT_EXTRA, mOriginalTextInput.getText().toString());

        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                ActivityTranslator.this,
                mLanguageInputContainer,
                mLanguageInputContainer.getTransitionName()
        );

        ActivityCompat.startActivityForResult(this, intent,
                EDIT_TEXT_ACTIVITY_REQUEST_CODE, activityOptions.toBundle());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case EDIT_TEXT_ACTIVITY_REQUEST_CODE:
                    mOriginalTextInput.setText(data.getStringExtra(ORIGINAL_TEXT_EXTRA));
                    break;

                case ANALYZE_PHOTO_ACTIVITY_REQUEST_CODE:
                    mOriginalTextInput.setText(data.getStringExtra(ActivityImageAnalyze.ARG_ANALYZE_RESULT));
                    break;
            }
        }
    }

    @OnClick(R.id.main_bottom_button_image)
    protected void captureImage() {
        Intent intent = new Intent(this, ActivityCloudSightAnalyze.class);
        startActivityForResult(intent, ANALYZE_PHOTO_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onHistoryItemSelected(HistoryItem item) {
        TranslationDirection direction = new TranslationDirection(item.getLang());
        App.getInstance().setTranslationDirection(direction);
        mLanguagePicker.updateDirection();
        mOriginalTextInput.setText(item.getOriginal());
        mScrollView.scrollTo(0, 0);
    }
}
