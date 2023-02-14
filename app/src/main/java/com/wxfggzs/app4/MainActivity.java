package com.wxfggzs.app4;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.wxfggzs.view.level.LevelView;

@SuppressWarnings("all")
public class MainActivity extends AppCompatActivity {
    LevelView _LevelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _LevelView = findViewById(R.id._LevelView);
        _LevelView.onResume();
    }
}