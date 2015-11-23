package com.niveshpc.weatherinfo;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;


/**
 * Created by LNJPC on 05-11-2015.
 */
public class Settings_Fragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         addPreferencesFromResource(R.xml.settings_preferences);

        //For all preferences attach an onPreferenceChangeListner so the UI summary can be updated when the preference changes
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_location_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_units_key)));

    }

    private void bindPreferenceSummaryToValue(Preference preference) {

        //set the listener to watch out for changes to this particular Location Preference
        preference.setOnPreferenceChangeListener(this);

        //Trigger the listener
        onPreferenceChange(preference, PreferenceManager
                .getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(),""));



    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        preference.setSummary(newValue.toString());
        return true;
    }






}

