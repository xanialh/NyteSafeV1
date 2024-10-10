package com.example.nytesafev0;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {

    private Switch nightModeSwitch;
    private Switch notificationsSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        nightModeSwitch = findViewById(R.id.nightModeSwitch);
        notificationsSwitch = findViewById(R.id.notificationsSwitch);

        nightModeSwitch.setChecked(getNightModePref());
        notificationsSwitch.setChecked(getNotificationsPref());

        nightModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                saveNightModePref(isChecked);
            }
        });

        notificationsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveNotificationsPref(isChecked);
                // Add logic to enable/disable notifications
            }
        });
    }

    private void saveNightModePref(boolean isChecked) {
        SharedPreferences.Editor editor = getSharedPreferences("AppSettings", MODE_PRIVATE).edit();
        editor.putBoolean("NightMode", isChecked);
        editor.apply();
    }

    private boolean getNightModePref() {
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        return prefs.getBoolean("NightMode", false);
    }

    private void saveNotificationsPref(boolean isChecked) {
        SharedPreferences.Editor editor = getSharedPreferences("AppSettings", MODE_PRIVATE).edit();
        editor.putBoolean("Notifications", isChecked);
        editor.apply();
    }

    private boolean getNotificationsPref() {
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        return prefs.getBoolean("Notifications", true);
    }
}
