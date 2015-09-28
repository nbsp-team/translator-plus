package com.nbsp.translator.ui;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.nbsp.translator.App;
import com.nbsp.translator.R;
import com.nbsp.translator.ThemeManager;
import com.nbsp.translator.api.ApiTranslator;
import com.nbsp.translator.data.History;
import com.nbsp.translator.data.HistoryItem;
import com.nbsp.translator.event.ThemeChangeEvent;
import com.nbsp.translator.models.TranslationDirection;
import com.nbsp.translator.models.TranslationTask;
import com.nbsp.translator.models.theme.Theme;
import com.nbsp.translator.models.yandextranslator.DetectedLanguage;
import com.nbsp.translator.models.yandextranslator.TranslateResult;
import com.nbsp.translator.ui.fragment.FragmentHistory;
import com.nbsp.translator.ui.fragment.FragmentLanguagePicker;
import com.nbsp.translator.ui.fragment.FragmentTranslationResult;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class ActivityTranslator extends BaseActivity implements FragmentHistory.OnHistoryItemSelectedListener, FragmentTranslationResult.OnDetectedLanguageClickListener {
    public static final String ORIGINAL_TEXT_EXTRA = "text";
    private static final int REQUEST_CODE_EDIT_TEXT_ACTIVITY = 0;
    private static final int REQUEST_CODE_TEXT_RECOGNIZE_ACTIVITY= 1;
    private static final int REQUEST_CODE_ANALYZE_PHOTO_ACTIVITY = 2;

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

    @Bind(R.id.toolbar)
    protected View mToolbarContainer;

    private FragmentLanguagePicker mLanguagePicker;
    private Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translator);
        ButterKnife.bind(this);

        mLanguagePicker = (FragmentLanguagePicker) getFragmentManager().findFragmentById(R.id.language_picker);

        Observable<TranslateResult> resultObservable = getTranslationTaskObservable()
                .debounce(350, TimeUnit.MILLISECONDS)
                .filter(translationTask -> translationTask.getTextToTranslate().length() > 0)
                .doOnNext(translationDirection -> setProgress(true))
                .switchMap(task -> ApiTranslator.getInstance().translate(task))
                .doOnNext(result -> setProgress(false));

        FragmentTranslationResult translationCard = (FragmentTranslationResult) getFragmentManager().findFragmentById(R.id.translation_result_card);
        Subscription translationCardSubscription = translationCard.subscribe(resultObservable);

        Subscription saveToHistorySubscription = resultObservable
                .debounce(1000, TimeUnit.MILLISECONDS)
                .map(result -> new HistoryItem(
                        mOriginalTextInput.getText().toString(),
                        result.getText(),
                        result.getLang()
                ))
                .filter(item -> item.getOriginal().length() > 0 && item.getTranslate().length() > 0)
                .subscribe(historyItem -> {
                    History.putObject(ActivityTranslator.this, historyItem);
                });

        Subscription hideResultSubscription = getTranslationTaskObservable()
                .filter(translationTask -> translationTask.getTextToTranslate().length() == 0)
                .subscribe(translationTask -> {
                    mResultContainer.setVisibility(View.GONE);
                });

        mSubscription = new CompositeSubscription(
                translationCardSubscription,
                saveToHistorySubscription,
                hideResultSubscription
        );
    }

    @OnClick(R.id.main_bottom_button_image)
    protected void captureImage() {
        Intent intent = new Intent(this, ActivityCloudSightAnalyze.class);
        startActivityForResult(intent, REQUEST_CODE_ANALYZE_PHOTO_ACTIVITY);
    }

    @OnClick(R.id.main_bottom_button_mic)
    protected void speachToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, App.getInstance().getTranslationDirection().getFrom().getLanguageCode());
        try {
            startActivityForResult(intent, REQUEST_CODE_TEXT_RECOGNIZE_ACTIVITY );
        } catch (Exception ignored) {}
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
                REQUEST_CODE_EDIT_TEXT_ACTIVITY, activityOptions.toBundle());
    }

    @Subscribe
    @Override
    public void colorize(ThemeChangeEvent event) {
        Theme currentTheme = ThemeManager.getInstance(getApplicationContext()).getCurrentTheme();
        getWindow().setStatusBarColor(currentTheme.getPrimaryDarkColor());
        mToolbarContainer.setBackgroundColor(currentTheme.getPrimaryColor());
    }

    public Observable<TranslationTask> getTranslationTaskObservable() {
        Observable<String> originalTextObservable = RxTextView.textChanges(mOriginalTextInput).map(CharSequence::toString);
        Observable<TranslationDirection> languageDirectionObservable = mLanguagePicker.getObservable();

        return Observable.combineLatest(
                languageDirectionObservable,
                originalTextObservable,
                (direction, text) -> new TranslationTask(text, direction)
        );
    }

    private void setProgress(boolean progress) {
        runOnUiThread(() -> {
            if (progress) {
                mLoadingProgressBar.setVisibility(View.VISIBLE);
                mResultContainer.setVisibility(View.GONE);
            } else {
                mLoadingProgressBar.setVisibility(View.GONE);
                mResultContainer.setVisibility(View.VISIBLE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_EDIT_TEXT_ACTIVITY:
                    mOriginalTextInput.setText(data.getStringExtra(ORIGINAL_TEXT_EXTRA));
                    break;

                case REQUEST_CODE_ANALYZE_PHOTO_ACTIVITY:
                    mOriginalTextInput.setText(data.getStringExtra(ActivityImageAnalyze.ARG_ANALYZE_RESULT));
                    break;

                case REQUEST_CODE_TEXT_RECOGNIZE_ACTIVITY:
                    ArrayList<String> thingsYouSaid = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (thingsYouSaid != null && !thingsYouSaid.isEmpty()) {
                        mOriginalTextInput.setText(thingsYouSaid.get(0));
                    }
                    break;
            }
        }
    }

    @Override
    public void onHistoryItemSelected(HistoryItem item) {
        TranslationDirection direction = new TranslationDirection(item.getLang());
        App.getInstance().setTranslationDirection(direction);
        mLanguagePicker.updateDirection();
        mOriginalTextInput.setText(item.getOriginal());
        mScrollView.scrollTo(0, 0);
    }

    @Override
    public void onDetectedLanguageClicked(DetectedLanguage detectedLanguage) {
        TranslationDirection currentDirection = App.getInstance().getTranslationDirection();

        if (currentDirection.getTo().equals(detectedLanguage.getLanguage())) {
            currentDirection.swap();
            App.getInstance().setTranslationDirection(currentDirection);
        } else {
            App.getInstance().setTranslationDirection(new TranslationDirection(detectedLanguage.getLanguage(), currentDirection.getFrom()));
        }

        mLanguagePicker.updateDirection();
    }
}
