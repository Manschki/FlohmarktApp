package com.example.mseifriedsberger16.flohmarktapp;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

public class preferences extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}