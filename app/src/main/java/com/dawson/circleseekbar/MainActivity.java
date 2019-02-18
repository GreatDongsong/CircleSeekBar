package com.dawson.circleseekbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CircularContinuousDotSeekBar circularContinuousDotSeekBar = findViewById(R.id.cs);
        circularContinuousDotSeekBar.setProgress(50);
    }
}
