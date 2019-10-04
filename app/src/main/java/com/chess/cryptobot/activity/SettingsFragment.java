package com.chess.cryptobot.activity;

import android.os.Bundle;


import androidx.preference.PreferenceFragmentCompat;

import com.chess.cryptobot.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
    }
}