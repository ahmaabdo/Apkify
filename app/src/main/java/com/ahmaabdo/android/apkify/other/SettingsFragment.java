package com.ahmaabdo.android.apkify.other;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.ahmaabdo.android.apkify.R;
import com.ahmaabdo.android.apkify.utils.AppPreferences;
import com.ahmaabdo.android.apkify.utils.DialogUtils;

/**
 * Created by Ahmad on Jul 22, 2017.
 */

public final class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Preference prefCustomPath;
    private ListPreference prefFilename;
    private ListPreference prefSortMode;
    private ListPreference prefTheme;

    AppPreferences appPreferences;
    Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        appPreferences = App.getAppPreferences();
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        context = getActivity();

        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());

        prefs.registerOnSharedPreferenceChangeListener(this);

        prefCustomPath = findPreference("prefCustomPath");
        prefFilename = (ListPreference) findPreference("prefFilename");
        prefSortMode = (ListPreference) findPreference("prefSortMode");
        prefTheme = (ListPreference) findPreference("prefTheme");

        //removes settings that wont work on lower versions
        Preference prefNavigationColor = findPreference("prefNavigationColor");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            prefNavigationColor.setEnabled(false);

        Preference prefReset = findPreference("prefReset");
        prefReset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(getActivity());
                sharedPreferences.edit().clear().apply();
                return true;
            }
        });

        prefCustomPath.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                DialogUtils.chooseDirectory(context).show();
                return true;
            }
        });

        setSortModeSummary();
        setThemeSummary();
        setCustomPathSummary();
        setFilenameSummary();
    }

    private void setCustomPathSummary() {

        prefCustomPath.setSummary(appPreferences.getCustomPath());
    }

    private void setFilenameSummary() {
        int filenameValue = Integer.valueOf(appPreferences.getFilename());
        prefFilename.setSummary(
                getResources().getStringArray(R.array.filenameEntries)[filenameValue]);
    }

    private void setSortModeSummary() {
        int sortValue = Integer.valueOf(appPreferences.getSortMode());
        prefSortMode.setSummary(
                getResources().getStringArray(R.array.sortEntries)[sortValue]);
    }

    private void setThemeSummary() {
        int themeValue = Integer.valueOf(appPreferences.getTheme());
        prefTheme.setSummary(getResources().getStringArray(R.array.themeEntries)[themeValue]);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (isAdded()) {
            Preference pref = findPreference(key);
            if (pref == prefCustomPath) {
                setCustomPathSummary();
            } else if (pref == prefFilename) {
                setFilenameSummary();
            } else if (pref == prefSortMode) {
                setSortModeSummary();
            } else if (pref == prefTheme) {
                setThemeSummary();
            }
        }
    }

}