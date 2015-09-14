package com.nbsp.translator.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.nbsp.translator.R;
import com.nbsp.translator.api.Languages;
import com.nbsp.translator.models.Language;
import com.nbsp.translator.models.TranslationDirection;
import com.nbsp.translator.ui.adapter.LanguagePickerAdapter;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;

public class FragmentLanguagePicker extends Fragment {
    public static final int MAGIC_CONSTANT = 34;
    private OnLanguagePickerEventsListener mListener;

    @Bind(R.id.language_from)
    protected Spinner mFromSpinner;

    @Bind(R.id.language_to)
    protected Spinner mToSpinner;

    @Bind(R.id.btn_swap)
    protected View mSwapButton;
    private Subscriber<? super TranslationDirection> mDirectionChangedSubscriber;

    public static FragmentLanguagePicker newInstance() {
        return new FragmentLanguagePicker();
    }

    public FragmentLanguagePicker() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_language_picker, container, false);
        ButterKnife.bind(this, view);

        SpinnerAdapter fromAdapter = new LanguagePickerAdapter();
        SpinnerAdapter toAdapter = new LanguagePickerAdapter();

        mFromSpinner.setAdapter(fromAdapter);
        mToSpinner.setAdapter(toAdapter);

        bindSpinners();

        mSwapButton.setOnClickListener(v -> swapLanguages());

        loadDirection();

        return view;
    }

    private void bindSpinners() {
        mFromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Languages.getInstance().getTranslationDirection().setFrom(
                        Languages.getInstance().getLanguages().get(position)
                );
                onDirectionChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mToSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Languages.getInstance().getTranslationDirection().setTo(
                        Languages.getInstance().getLanguages().get(position)
                );
                onDirectionChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void swapLanguages() {
        Animation rotateButton = AnimationUtils.loadAnimation(getActivity(), R.anim.language_swap_rotate);
        Animation fadeOutLeft = AnimationUtils.loadAnimation(getActivity(), R.anim.language_spinner_left_out);
        Animation fadeOutRight = AnimationUtils.loadAnimation(getActivity(), R.anim.language_spinner_right_out);
        Animation fadeInLeft = AnimationUtils.loadAnimation(getActivity(), R.anim.language_spinner_left_in);
        Animation fadeInRight = AnimationUtils.loadAnimation(getActivity(), R.anim.language_spinner_right_in);

        mSwapButton.startAnimation(rotateButton);

        fadeOutLeft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Languages.getInstance().getTranslationDirection().swap();
                loadDirection();

                mFromSpinner.startAnimation(fadeInLeft);
                mToSpinner.startAnimation(fadeInRight);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mFromSpinner.startAnimation(fadeOutLeft);
        mToSpinner.startAnimation(fadeOutRight);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDirection();
    }

    private void loadDirection() {
        List<Language> languages = Languages.getInstance().getLanguages();
        TranslationDirection translationDirection = Languages.getInstance().getTranslationDirection();
        mFromSpinner.setSelection(languages.indexOf(translationDirection.getFrom()));
        mToSpinner.setSelection(languages.indexOf(translationDirection.getTo()));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnLanguagePickerEventsListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLanguagePickerEventsListener");
        }

        mListener.onCreateChangeObservable(Observable.create(new Observable.OnSubscribe<TranslationDirection>() {
            @Override
            public void call(Subscriber<? super TranslationDirection> subscriber) {
                mDirectionChangedSubscriber = subscriber;
            }
        }).debounce(MAGIC_CONSTANT, TimeUnit.MILLISECONDS).skip(1));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mDirectionChangedSubscriber != null) {
            mDirectionChangedSubscriber.onCompleted();
            mDirectionChangedSubscriber = null;
        }
        mListener = null;
    }

    private void onDirectionChanged() {
        if (mDirectionChangedSubscriber != null) {
            mDirectionChangedSubscriber.onNext(
                    Languages.getInstance().getTranslationDirection()
            );
        }
    }

    public interface OnLanguagePickerEventsListener {
        void onCreateChangeObservable(Observable<TranslationDirection> observable);
    }

}
