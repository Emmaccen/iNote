package com.example.notepad;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import androidx.savedstate.SavedStateRegistry;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
            String key = "restore";
            if(preference.getKey().equals(key)){
                AlertDialog.Builder restoreAlert = new AlertDialog.Builder(preference.getContext());
                restoreAlert.setIcon(R.drawable.ic_restore_24dp);
                restoreAlert.setTitle(getString(R.string.restore_settings_default));
                restoreAlert.setMessage(getString(R.string.alert_restoredialoge_message));
                restoreAlert.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        restoreDefault();
                    }
                });
                restoreAlert.setNegativeButton(getString(R.string.cancel),null);
                restoreAlert.show();
            }
            return super.onPreferenceTreeClick(preference);
        }

        private void restoreDefault() {
//            SwitchPreference switchPreference =  getPreferenceScreen().findPreference("sync");
//            switchPreference.setChecked(false);
//            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
//            SharedPreferences.Editor editor = preferences.edit();
//            editor.putBoolean("sync", false);
//            editor.apply();
            Toast.makeText(getContext(),getString(R.string.default_settings_toast_message), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }
}