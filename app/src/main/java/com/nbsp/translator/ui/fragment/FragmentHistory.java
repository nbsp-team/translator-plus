package com.nbsp.translator.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nbsp.translator.R;
import com.nbsp.translator.data.History;
import com.nbsp.translator.data.HistoryItem;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by nickolay on 26.09.15.
 */

public class FragmentHistory extends Fragment {
    public static final int HISTORY_ITEMS_NUMBER = 25;

    @Bind(R.id.history_list)
    protected LinearLayout mHistoryLayout;

    @Bind(R.id.history_card)
    protected CardView mHistoryContainerCard;

    private OnHistoryItemSelectedListener mListener;
    private Subscription mSubscription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() instanceof OnHistoryItemSelectedListener) {
            mListener = (OnHistoryItemSelectedListener) getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, view);

        mSubscription = History.getLastItems(getActivity(), HISTORY_ITEMS_NUMBER)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setHistory);

        return view;
    }

    private void setHistory(List<HistoryItem> historyItems) {
        mHistoryLayout.removeAllViews();

        setVisiblilityCardContainer(!historyItems.isEmpty());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        for (int i = 0; i < historyItems.size(); i++) {
            HistoryItem item = historyItems.get(i);

            View itemView = inflater.inflate(R.layout.item_history, mHistoryLayout, false);
            TextView originalText = (TextView) itemView.findViewById(R.id.original);
            TextView translateText = (TextView) itemView.findViewById(R.id.translate);

            originalText.setText(item.getOriginal());
            translateText.setText(item.getTranslate());

            mHistoryLayout.addView(itemView);

            if (i != historyItems.size() - 1) {
                View separator = inflater.inflate(R.layout.item_separator, mHistoryLayout, false);
                mHistoryLayout.addView(separator);
            }

            itemView.setOnClickListener(view -> {
                if (mListener != null) {
                    mListener.onHistoryItemSelected(item);
                }
            });
        }
    }

    private void setVisiblilityCardContainer(boolean visibility) {
        mHistoryContainerCard.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mSubscription.unsubscribe();
    }

    public interface OnHistoryItemSelectedListener {
        void onHistoryItemSelected(HistoryItem item);
    }
}
