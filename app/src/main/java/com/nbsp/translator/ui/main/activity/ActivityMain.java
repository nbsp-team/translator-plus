package com.nbsp.translator.ui.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.nbsp.translator.R;
import com.nbsp.translator.api.Api;
import com.nbsp.translator.models.Translate;
import com.nbsp.translator.ui.translator.activity.ActivityTranslator;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.schedulers.Schedulers;

public class ActivityMain extends AppCompatActivity {

    @Bind(R.id.language_input_container)
    protected LinearLayout mLanguageInput;

    @Bind(R.id.languages_bar)
    protected LinearLayout mLanguagesBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @SuppressWarnings("unchecked")
    @OnClick(R.id.language_input_container)
    protected void startTranslatorWithAnimation() {
        Intent intent = new Intent(getApplicationContext(), ActivityTranslator.class);

        Pair<View, String> p1 = Pair.create((View) mLanguageInput, mLanguageInput.getTransitionName());
        Pair<View, String> p2 = Pair.create(mLanguagesBar, mLanguagesBar.getTransitionName());

        ActivityOptionsCompat activityOptions = ActivityOptionsCompat
                .makeSceneTransitionAnimation(ActivityMain.this, p1, p2);

        ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
    }
}
