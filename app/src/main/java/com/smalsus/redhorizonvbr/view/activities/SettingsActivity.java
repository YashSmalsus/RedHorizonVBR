package com.smalsus.redhorizonvbr.view.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.view.View;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.view.SeekBarPreference;

/**
 * QuickBlox team
 */
public class SettingsActivity extends BaseActivity implements SharedPreferences.OnSharedPreferenceChangeListener {


    private static final int MAX_VIDEO_START_BITRATE = 2000;
    private String bitrateStringKey;


    public static void start(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initActionBar();


        bitrateStringKey = getString(R.string.pref_startbitratevalue_key);
    }

    private void initActionBar() {
        actionBar.setTitle(R.string.actionbar_title_settings);
    }

    @Override
    protected View getSnackbarAnchorView() {
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(bitrateStringKey)) {
            int bitrateValue = sharedPreferences.getInt(bitrateStringKey, Integer.parseInt(
                    getString(R.string.pref_startbitratevalue_default)));
            if (bitrateValue == 0) {
                setDefaultstartingBitrate(sharedPreferences);
                return;
            }
            int startBitrate = bitrateValue;
            if (startBitrate > MAX_VIDEO_START_BITRATE) {
                //Toaster.longToast("Max value is:" + MAX_VIDEO_START_BITRATE);
                setDefaultstartingBitrate(sharedPreferences);
            }
        }
    }

    private void setDefaultstartingBitrate(SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(bitrateStringKey,
                Integer.parseInt(getString(R.string.pref_startbitratevalue_default)));
        editor.apply();
        updateSummary(sharedPreferences, bitrateStringKey);
    }

    private void updateSummary(SharedPreferences sharedPreferences, String key) {

    }
}