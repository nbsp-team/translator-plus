package com.nbsp.translator.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.nbsp.translator.R;
import com.nbsp.translator.api.Languages;
import com.nbsp.translator.models.Language;
import com.nbsp.translator.models.TranslationDirection;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FragmentLanguagePicker extends Fragment {
    private OnLanguagePickerEventsListener mListener;

    @Bind(R.id.language_from)
    protected Spinner mFromSpinner;

    @Bind(R.id.language_to)
    protected Spinner mToSpinner;

    @Bind(R.id.btn_swap)
    protected View mSwapButton;

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

        mSwapButton.setOnClickListener(v -> {
            Languages.getInstance().getTranslationDirection().swap();
            loadDirection();
            onDirectionChanged();
        });

        loadDirection();

        return view;
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void onDirectionChanged() {
        mListener.onTranslationDirectionChanged(
                Languages.getInstance().getTranslationDirection()
        );
    }

    public interface OnLanguagePickerEventsListener {
        void onTranslationDirectionChanged(TranslationDirection direction);
    }

}
