package com.dawson.circleseekbar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CircularContinuousDotSeekBar circularContinuousDotSeekBar = findViewById(R.id.cs);
        final TextView textView = findViewById(R.id.textView2);
        circularContinuousDotSeekBar.setProgressChangeListener(new CircularContinuousDotSeekBar.OnProgressControlListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onProgressChange(View view, int progress) {
                textView.setText(String.format("%d", progress));
            }

            @Override
            public void onProgressControlStart(View view) {

            }

            @Override
            public void onProgressControlEnd(View view) {

            }
        });
        circularContinuousDotSeekBar.setProgress(50);
    }
}
