package com.nbsp.translator.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.nbsp.translator.R;
import com.nbsp.translator.models.TranslationDirection;
import com.nbsp.translator.models.TranslationTask;
import com.nbsp.translator.ui.fragment.FragmentLanguagePicker;
import com.nbsp.translator.ui.fragment.FragmentTranslationCard;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;

public class ActivityTranslator extends AppCompatActivity {
    public static final String ORIGINAL_TEXT_EXTRA = "text";
    private static final int EDIT_TEXT_ACTIVITY_REQUEST_CODE = 0;
    private static final int ANALYZE_PHOTO_ACTIVITY_REQUEST_CODE = 1;

    @Bind(R.id.toolbar)
    protected View mToolbar;

    @Bind(R.id.original_text_input_container)
    protected LinearLayout mLanguageInputContainer;

    @Bind(R.id.original_text_input)
    protected EditText mOriginalTextInput;

    private Subscription mTranslationCardSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translator);
        ButterKnife.bind(this);

        Observable<String> originalTextObservable = RxTextView.textChanges(mOriginalTextInput).map(CharSequence::toString);
        FragmentLanguagePicker languagePicker = (FragmentLanguagePicker) getFragmentManager().findFragmentById(R.id.language_picker);
        Observable<TranslationDirection> languageDirectionObservable = languagePicker.getObservable();

        FragmentTranslationCard translationCard = (FragmentTranslationCard) getFragmentManager().findFragmentById(R.id.translation_result_card);
        mTranslationCardSubscription = translationCard.subscribe(Observable.combineLatest(
                languageDirectionObservable,
                originalTextObservable,
                (direction, text) -> new TranslationTask(text, direction)
        ));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mOriginalTextInput.setSelection(mOriginalTextInput.getText().length());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTranslationCardSubscription.unsubscribe();
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
}
