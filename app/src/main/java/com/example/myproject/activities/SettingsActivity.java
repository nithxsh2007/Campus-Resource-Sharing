package com.example.myproject.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myproject.R;
import com.example.myproject.utils.ThemeUtils;
import com.google.android.material.materialswitch.MaterialSwitch;

public class SettingsActivity extends AppCompatActivity {

    private MaterialSwitch switchDarkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchDarkMode = findViewById(R.id.switchSettingsDarkMode);

        switchDarkMode.setChecked(ThemeUtils.isDarkModeEnabled(this));

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ThemeUtils.setDarkMode(this, isChecked);
        });
    }
}
