package com.nbsp.translator.ui.fragment;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.nbsp.translator.R;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by nickolay on 26.09.15.
 */

public class FragmentPlayerButton extends Fragment {
    private enum State {
        DISABLED,
        NORMAL,
        PLAYING
    }

    @Bind(R.id.button)
    protected ImageButton mButton;

    private String mUrl;
    private MediaPlayer mMediaPlayer;
    private State mState = State.DISABLED;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_button, container, false);
        ButterKnife.bind(this, view);

        updateUi();

        return view;
    }

    @OnClick(R.id.button)
    protected void onButtonClick() {
        if (mState == State.NORMAL) {
            if (mMediaPlayer != null) {
                try {
                    mMediaPlayer.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(mediaPlayer -> {
                mState = State.NORMAL;
                updateUi();
            });

            try {
                mMediaPlayer.setDataSource(mUrl);
                mMediaPlayer.prepareAsync();
                mMediaPlayer.setOnPreparedListener(mediaPlayer -> {
                    mMediaPlayer.start();
                    mState = State.PLAYING;
                    updateUi();
                });
            } catch (IOException e) {
                mState = State.DISABLED;
                e.printStackTrace();
            }
        }

        if (mState == State.PLAYING) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                mState = State.NORMAL;
            } else {
                mState = State.NORMAL;
            }
        }

        updateUi();
    }

    public void setAudioUrl(String url) {
        if (url != null) {
            mUrl = url;
            mState = State.NORMAL;
        } else {
            mState = State.DISABLED;
        }
        updateUi();
    }

    private void updateUi() {
        int buttonIcon = R.drawable.ic_volume_up_white;
        switch (mState) {
            case DISABLED:
                buttonIcon = R.drawable.ic_volume_off_white_24dp;
                break;
            case NORMAL:
                buttonIcon = R.drawable.ic_volume_up_white;
                break;
            case PLAYING:
                buttonIcon = R.drawable.ic_stop_white_24dp;
                break;
        }

        mButton.setImageResource(buttonIcon);
    }
}
