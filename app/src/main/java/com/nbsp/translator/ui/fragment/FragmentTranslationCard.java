package com.nbsp.translator.ui.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nbsp.translator.App;
import com.nbsp.translator.R;
import com.nbsp.translator.ThemeManager;
import com.nbsp.translator.event.ThemeChangeEvent;
import com.nbsp.translator.models.TranslationDirection;
import com.nbsp.translator.models.theme.Theme;
import com.nbsp.translator.models.yandextranslator.TranslateResult;
import com.nbsp.translator.utils.UrlBuilder;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class FragmentTranslationCard extends BaseFragment {
    @Bind(R.id.result_to)
    protected TextView mResultTo;

    @Bind(R.id.result_language_to)
    protected TextView mLanguageTo;

    @Bind(R.id.container_language_to)
    protected CardView mContainerLanguageTo;

    private FragmentPlayerButton mPlayerFragment;

    public FragmentTranslationCard() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translation_card, container, false);
        ButterKnife.bind(this, view);

        mPlayerFragment = (FragmentPlayerButton) getChildFragmentManager().findFragmentById(R.id.player);
        return view;
    }

    @Override
    @Subscribe
    public void colorize(ThemeChangeEvent event) {
        Theme currentTheme = ThemeManager.getInstance(getActivity()).getCurrentTheme();
        mContainerLanguageTo.setCardBackgroundColor(currentTheme.getPrimaryColor());
    }

    public Subscription subscribe(Observable<TranslateResult> observable) {
        return observable
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
                        showResultCard(translateResult);
                    }
                });
    }

    @OnClick(R.id.copy_to_button)
    protected void copyResultToClipboard(View view) {
        if (!mResultTo.getText().toString().isEmpty()) {
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", mResultTo.getText().toString());
            clipboard.setPrimaryClip(clip);

            Toast.makeText(getActivity(), R.string.text_copied, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateViews(String languageName, String result) {
        mLanguageTo.setText(languageName.toUpperCase());
        mResultTo.setText(result);
    }

    private void showResultCard(TranslateResult result) {
        TranslationDirection translationDirection = App.getInstance().getTranslationDirection();
        updateViews(translationDirection.getTo().getName(), result.getText());

        String ttsUrl = UrlBuilder.getGoogleTranslateTtlUri(
                mResultTo.getText().toString(),
                App.getInstance().getTranslationDirection().getTo().getLanguageCode()
        ).toString();
        mPlayerFragment.setAudioUrl(ttsUrl);
    }
}
