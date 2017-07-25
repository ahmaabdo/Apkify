package com.ahmaabdo.android.apkify.other;

import android.app.Application;

import com.ahmaabdo.android.apkify.utils.AppPreferences;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.Iconics;

/**
 * Created by Ahmad on Jul 22, 2017.
 */

public class App extends Application {
    private static AppPreferences appPreferences;
    private static int currentAdapter;

    @Override
    public void onCreate() {
        super.onCreate();

        //set fields
        appPreferences = new AppPreferences(this);
        currentAdapter = 0;

        //register custom fonts
        Iconics.registerFont(new GoogleMaterial());
    }

    public static AppPreferences getAppPreferences() {
        return appPreferences;
    }

    public static int getCurrentAdapter() {
        return currentAdapter;
    }

    public static void setCurrentAdapter(int value) {
        currentAdapter = value;
    }
}
