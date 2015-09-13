package com.nbsp.translator.ui.main.activity;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nbsp.translator.R;
import com.nbsp.translator.models.TranslationDirection;

public class LanguagePicker extends Fragment {
    private static final String ARG_TRANSLATION_DIRECTION = "translation_direction";

    private TranslationDirection mDirection;

    private OnLanguagePickerEventsListener mListener;

    public static LanguagePicker newInstance(TranslationDirection translationDirection) {
        LanguagePicker fragment = new LanguagePicker();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TRANSLATION_DIRECTION, translationDirection);
        fragment.setArguments(args);
        return fragment;
    }

    public LanguagePicker() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDirection = (TranslationDirection) getArguments().getSerializable(ARG_TRANSLATION_DIRECTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_language_picker, container, false);
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

    public interface OnLanguagePickerEventsListener {
        void onTranslationDirectionChanged(TranslationDirection direction);
    }

}
