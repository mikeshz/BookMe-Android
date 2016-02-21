package com.nuance.speechkitsample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Initial screen.
 * <p>
 * Copyright (c) 2015 Nuance Communications. All rights reserved.
 */
public class MainActivity extends Activity implements View.OnClickListener {

    View asrButton = null;
    View nluButton = null;
    View textNluButton = null;
    View ttsButton = null;

    View audioButton = null;

    View configButton = null;
    View aboutButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout mainContent = (LinearLayout) findViewById(R.id.main_content);

        LinearLayout coreTech = inflateCategoryView("CORE TECHNOLOGIES", mainContent);

        //asrButton = inflateRowView("Speech Recognition", "Cloud based ASR", coreTech);
        nluButton = inflateRowView("Make a reservation through speech", "", coreTech);

    }

    @Override
    public void onClick(View v) {

        Intent intent = null;
        if (v == nluButton) {
            intent = new Intent(this, NLUActivity.class);
        } else if (v == textNluButton) {
            intent = new Intent(this, TextNLUActivity.class);
        } else if (v == ttsButton) {
            intent = new Intent(this, TTSActivity.class);
        } else if (v == audioButton) {
            intent = new Intent(this, AudioActivity.class);
        }

        if (intent != null) {
            startActivity(intent);
            overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
        }
    }

    private LinearLayout inflateCategoryView(String title, LinearLayout parent) {
        View v = (View) getLayoutInflater().inflate(R.layout.activity_main_category, null);
        ((TextView) v.findViewById(R.id.title)).setText(title);
        parent.addView(v);
        return ((LinearLayout) v.findViewById(R.id.list));
    }

    private View inflateRowView(String mainText, String subText, LinearLayout parent) {
        View v = (View) getLayoutInflater().inflate(R.layout.activity_main_row, null);
        ((TextView) v.findViewById(R.id.mainText)).setText(mainText);
        ((TextView) v.findViewById(R.id.subText)).setText(subText);
        parent.addView(v);
        v.setOnClickListener(this);
        return v;
    }
}
