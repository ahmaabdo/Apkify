package com.ahmaabdo.android.apkify;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ahmaabdo.android.apkify.other.App;
import com.ahmaabdo.android.apkify.utils.AppPreferences;

/**
 * Created by Ahmad on Jul 22, 2017.
 */

public abstract class ThemeActivity extends AppCompatActivity {

    private AppPreferences appPreferences;
    private String currentTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        appPreferences = App.getAppPreferences();
        currentTheme = appPreferences.getTheme();
        if (appPreferences.getTheme().equals("1")) {
            setTheme(R.style.Light);
        } else {
            setTheme(R.style.Dark);
            setTheme(R.style.DrawerDark);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!appPreferences.getTheme().equals(currentTheme)) {
            //TODO: call recreate() instead of restart
            //try also getApplicationContext()
            restart();
        }
    }

    protected void restart() {
        Intent intent = new Intent(this, getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        overridePendingTransition(0, 0);
        startActivity(intent);

    }
}
