package com.hbisoft.hbrecorderexample;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // load settings fragment
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();
    }

    public static class MainPreferenceFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener{
        ListPreference key_video_resolution, key_audio_source, key_video_encoder, key_video_fps, key_video_bitrate, key_output_format;
        SwitchPreference key_record_audio;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);

            key_record_audio = findPreference(getString(R.string.key_record_audio));

            key_audio_source = findPreference(getString(R.string.key_audio_source));
            if (key_audio_source != null) {
                key_audio_source.setOnPreferenceChangeListener(this);
            }

            key_video_encoder = findPreference(getString(R.string.key_video_encoder));
            if (key_video_encoder != null) {
                key_video_encoder.setOnPreferenceChangeListener(this);
            }

            key_video_resolution = findPreference(getString(R.string.key_video_resolution));
            if (key_video_resolution != null) {
                key_video_resolution.setOnPreferenceChangeListener(this);
            }

            key_video_fps = findPreference(getString(R.string.key_video_fps));
            if (key_video_fps != null) {
                key_video_fps.setOnPreferenceChangeListener(this);
            }

            key_video_bitrate = findPreference(getString(R.string.key_video_bitrate));
            if (key_video_bitrate != null) {
                key_video_bitrate.setOnPreferenceChangeListener(this);
            }

            key_output_format = findPreference(getString(R.string.key_output_format));
            if (key_output_format != null) {
                key_output_format.setOnPreferenceChangeListener(this);
            }

            setPreviousSelectedAsSummary();

        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String preferenceKey = preference.getKey();
            ListPreference listPreference;
            switch (preferenceKey) {
                case "key_audio_source":
                    listPreference = findPreference(getString(R.string.key_audio_source));
                    if (listPreference != null) {
                        listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue(newValue.toString())]);
                    }
                    break;
                case "key_video_encoder":
                    listPreference = findPreference(getString(R.string.key_video_encoder));
                    if (listPreference != null) {
                        listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue(newValue.toString())]);
                        listPreference.setValue(newValue.toString());
                    }
                    break;
                case "key_video_resolution":
                    listPreference = findPreference(getString(R.string.key_video_resolution));
                    if (listPreference != null) {
                        listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue(newValue.toString())]);
                        listPreference.setValue(newValue.toString());
                    }
                    break;
                case "key_video_fps":
                    listPreference = findPreference(getString(R.string.key_video_fps));
                    if (listPreference != null) {
                        listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue(newValue.toString())]);
                        listPreference.setValue(newValue.toString());
                    }

                    break;
                case "key_video_bitrate":
                    listPreference = findPreference(getString(R.string.key_video_bitrate));
                    if (listPreference != null) {
                        listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue(newValue.toString())]);
                        listPreference.setValue(newValue.toString());
                    }
                    break;
                case "key_output_format":
                    listPreference = findPreference(getString(R.string.key_output_format));
                    if (listPreference != null) {
                        listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue(newValue.toString())]);
                        listPreference.setValue(newValue.toString());
                    }
                    break;
            }

            return true;
        }

        private void setPreviousSelectedAsSummary() {
            if (getActivity() != null) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String video_resolution = prefs.getString("key_video_resolution", null);
                boolean audio_enabled = prefs.getBoolean("key_record_audio", true);
                String audio_source = prefs.getString("key_audio_source", null);
                String video_encoder = prefs.getString("key_video_encoder", null);
                String video_frame_rate = prefs.getString("key_video_fps", null);
                String video_bit_rate = prefs.getString("key_video_bitrate", null);
                String output_format = prefs.getString("key_output_format", null);

                /*Record Audio Prefs*/
                key_record_audio.setChecked(audio_enabled);

                /*Audio Source Prefs*/
                if (audio_source != null) {
                    int index = key_audio_source.findIndexOfValue(audio_source);
                    key_audio_source.setSummary(key_audio_source.getEntries()[index]);

                } else {
                    String defaultSummary = PreferenceManager.getDefaultSharedPreferences(key_audio_source.getContext()).getString(key_audio_source.getKey(), "");
                    key_audio_source.setSummary(defaultSummary);
                }

                /*Video Encoder Prefs*/
                if (video_encoder != null) {
                    int index = key_video_encoder.findIndexOfValue(video_encoder);
                    key_video_encoder.setSummary(key_video_encoder.getEntries()[index]);

                } else {
                    String defaultSummary = PreferenceManager.getDefaultSharedPreferences(key_video_encoder.getContext()).getString(key_video_encoder.getKey(), "");
                    key_video_encoder.setSummary(defaultSummary);
                }

                /*Video Resolution Prefs*/
                if (video_resolution != null) {
                    int index = key_video_resolution.findIndexOfValue(video_resolution);
                    key_video_resolution.setSummary(key_video_resolution.getEntries()[index]);

                } else {
                    String defaultSummary = PreferenceManager.getDefaultSharedPreferences(key_video_resolution.getContext()).getString(key_video_resolution.getKey(), "");
                    key_video_resolution.setSummary(defaultSummary);
                }

                /*Video Frame Rate Prefs*/
                if (video_frame_rate != null) {
                    int index = key_video_fps.findIndexOfValue(video_frame_rate);
                    key_video_fps.setSummary(key_video_fps.getEntries()[index]);

                } else {
                    String defaultSummary = PreferenceManager.getDefaultSharedPreferences(key_video_fps.getContext()).getString(key_video_fps.getKey(), "");
                    key_video_fps.setSummary(defaultSummary);
                }

                /*Video Bit Rate Prefs*/
                if (video_bit_rate != null) {
                    int index = key_video_bitrate.findIndexOfValue(video_bit_rate);
                    key_video_bitrate.setSummary(key_video_bitrate.getEntries()[index]);

                } else {
                    String defaultSummary = PreferenceManager.getDefaultSharedPreferences(key_video_bitrate.getContext()).getString(key_video_bitrate.getKey(), "");
                    key_video_bitrate.setSummary(defaultSummary);
                }

                /*Output Format Prefs*/
                if (output_format != null) {
                    int index = key_output_format.findIndexOfValue(output_format);
                    key_output_format.setSummary(key_output_format.getEntries()[index]);

                } else {
                    String defaultSummary = PreferenceManager.getDefaultSharedPreferences(key_output_format.getContext()).getString(key_output_format.getKey(), "");
                    key_output_format.setSummary(defaultSummary);
                }

            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
