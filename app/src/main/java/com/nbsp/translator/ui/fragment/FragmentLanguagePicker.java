package com.nbsp.translator.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.nbsp.translator.App;
import com.nbsp.translator.R;
import com.nbsp.translator.Languages;
import com.nbsp.translator.models.TranslationDirection;
import com.nbsp.translator.models.Language;
import com.nbsp.translator.ui.adapter.LanguagePickerAdapter;
import com.nbsp.translator.ui.rxbinding.LanguagePickerOnSubscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;

public class FragmentLanguagePicker extends Fragment {
    public static final int MAGIC_CONSTANT = 42;

    @Bind(R.id.language_from)
    protected Spinner mFromSpinner;

    @Bind(R.id.language_to)
    protected Spinner mToSpinner;

    @Bind(R.id.btn_swap)
    protected View mSwapButton;

    private List<DirectionChangedListener> mListeners = new ArrayList<>();

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
        updateDirection();

        return view;
    }

    private void bindSpinners() {
        mFromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                App.getInstance().getTranslationDirection().setFrom(
                        Languages.getInstance().getLanguages().get(position)
                );
                updateDirection();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mToSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                App.getInstance().getTranslationDirection().setTo(
                        Languages.getInstance().getLanguages().get(position)
                );
                updateDirection();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @OnClick(R.id.btn_swap)
    protected void swapLanguages() {
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
                App.getInstance().getTranslationDirection().swap();
                updateDirection();

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

    public void updateDirection() {
        List<Language> languages = Languages.getInstance().getLanguages();
        TranslationDirection direction = App.getInstance().getTranslationDirection();
        mFromSpinner.setSelection(languages.indexOf(direction.getFrom()));
        mToSpinner.setSelection(languages.indexOf(direction.getTo()));
        onDirectionChanged();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListeners.clear();
    }

    private void onDirectionChanged() {
        for(DirectionChangedListener listener : mListeners) {
            listener.onDirectionChanged(App.getInstance().getTranslationDirection());
        }
    }

    public void addDirectionChangedListener(DirectionChangedListener listener) {
        mListeners.add(listener);
    }

    public void removeDirectionChangedListener(DirectionChangedListener listener) {
        mListeners.remove(listener);
    }

    public interface DirectionChangedListener {
        void onDirectionChanged(TranslationDirection direction);
    }

    public Observable<TranslationDirection> getObservable() {
        return Observable.create(new LanguagePickerOnSubscribe(this));
    }
}
