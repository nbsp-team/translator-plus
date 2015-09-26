package com.nbsp.translator.ui.fragment;

import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nbsp.translator.App;
import com.nbsp.translator.R;
import com.nbsp.translator.api.ApiTranslator;
import com.nbsp.translator.models.TranslationDirection;
import com.nbsp.translator.models.TranslationTask;
import com.nbsp.translator.models.yandextranslator.TranslateResult;
import com.nbsp.translator.utils.UrlBuilder;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class FragmentTranslationCard extends Fragment {

    @Bind(R.id.loading_progress_bar)
    protected ProgressBar mLoadingProgressBar;

    @Bind(R.id.result_to)
    protected TextView mResultTo;

    @Bind(R.id.result_language_to)
    protected TextView mLanguageTo;

    @Bind(R.id.container_language_to)
    protected View mContainerLanguageTo;

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

    public Subscription subscribe(Observable<TranslationTask> observable) {
        // TODO: show progress in activity
        return observable.debounce(350, TimeUnit.MILLISECONDS)
                .doOnNext(translationDirection -> showProgress())
                .switchMap(task -> ApiTranslator.getInstance().translate(task))
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

            Toast.makeText(getActivity(), R.string.text_copyed, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateViews(String languageName, String result) {
        mLanguageTo.setText(languageName.toUpperCase());
        mResultTo.setText(result);
    }

    private void showProgress() {
        Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fade_out);
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

        getActivity().runOnUiThread(() -> mContainerLanguageTo.startAnimation(animation));
    }

    private void showResultCard(TranslateResult result) {
        TranslationDirection translationDirection = App.getInstance().getTranslationDirection();
        updateViews(translationDirection.getTo().getName(), result.getText());

        String ttsUrl = UrlBuilder.getGoogleTranslateTtlUri(
                mResultTo.getText().toString(),
                App.getInstance().getTranslationDirection().getTo().getLanguageCode()
        ).toString();
        mPlayerFragment.setAudioUrl(ttsUrl);

        Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fade_in);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mLoadingProgressBar.setVisibility(View.GONE);
                mContainerLanguageTo.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        mContainerLanguageTo.startAnimation(animation);
    }
}
