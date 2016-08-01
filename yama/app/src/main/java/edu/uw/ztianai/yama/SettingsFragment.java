package edu.uw.ztianai.yama;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Tianai Zhao on 16/04/23
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
}