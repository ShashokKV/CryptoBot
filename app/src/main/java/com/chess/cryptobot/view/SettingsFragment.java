package com.chess.cryptobot.view;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.chess.cryptobot.R;

class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}