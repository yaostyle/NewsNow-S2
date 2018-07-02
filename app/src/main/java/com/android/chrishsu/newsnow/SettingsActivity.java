package com.android.chrishsu.newsnow;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

// Create a SettingsActivity
public class SettingsActivity extends AppCompatActivity {
    // Override onCreate to connect settings_activity layout
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    // Create a PreferenceFragment and also with PreferenceChangeListener
    public static class NewsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
        // Override onCreate
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Add a preference screen by connectting R resource
            addPreferencesFromResource(R.xml.settings_main);

            // Create searchKeyword preference from R resource
            Preference searchKeyword = findPreference(getString(R.string.settings_search_keyword_key));
            // Bind the value by custom class bindPreferenceSummaryToValue
            bindPreferenceSummaryToValue(searchKeyword);

            // Create orderBy preference from R resource
            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            // Bind the value by custom class bindPreferenceSummaryToValue
            bindPreferenceSummaryToValue(orderBy);
        }

        // Override onPreferenceChange
        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            // Create a string object from current object var: o
            String stringValue = o.toString();

            // A switch statement to determine if it's a ListPreference or regular preference
            if (preference instanceof ListPreference) {
                // If it's a ListPreference, create an object
                ListPreference listPreference = (ListPreference) preference;

                // Get the current preference index from stringValue
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                // If index is equal or greater than zero
                if (prefIndex >= 0) {
                    // Create a label from the list entry
                    CharSequence[] labels = listPreference.getEntries();
                    // Set its value by current index of labels var
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                // Else, set its value by stringValue
                preference.setSummary(stringValue);
            }
            // Return true condition
            return true;
        }

        // Custom class to bind the value from preference
        private void bindPreferenceSummaryToValue(Preference preference) {
            // Get the current preference state
            preference.setOnPreferenceChangeListener(this);

            // Create current preference from the default R resource
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            // Get the string
            String preferenceString = preferences.getString(preference.getKey(), "");
            // Set a new preference
            onPreferenceChange(preference, preferenceString);
        }
    }
}
