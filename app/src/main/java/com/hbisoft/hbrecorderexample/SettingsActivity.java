package com.hbisoft.hbrecorderexample;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.MenuItem;
import android.widget.BaseAdapter;
import android.widget.Toast;

public class SettingsActivity extends AppCompatPreferenceActivity {
    private static final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // load settings fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();
    }

    public static class MainPreferenceFragment extends PreferenceFragment {
        ListPreference key_video_resolution, key_audio_source, key_video_encoder, key_video_fps, key_video_bitrate, key_output_format;
        SwitchPreference key_record_audio, key_show_notification;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);

            key_record_audio = (SwitchPreference) findPreference(getString(R.string.key_record_audio));

            key_audio_source = (ListPreference) findPreference(getString(R.string.key_audio_source));
            key_audio_source.setOnPreferenceChangeListener(audioSourceListener);

            key_video_encoder = (ListPreference) findPreference(getString(R.string.key_video_encoder));
            key_video_encoder.setOnPreferenceChangeListener(videoEncoderListener);

            key_video_resolution = (ListPreference) findPreference(getString(R.string.key_video_resolution));
            key_video_resolution.setOnPreferenceChangeListener(videoResolutionListener);

            key_video_fps = (ListPreference) findPreference(getString(R.string.key_video_fps));
            key_video_fps.setOnPreferenceChangeListener(videoFrameRateListener);

            key_video_bitrate = (ListPreference) findPreference(getString(R.string.key_video_bitrate));
            key_video_bitrate.setOnPreferenceChangeListener(videoBitRateListener);

            key_output_format = (ListPreference) findPreference(getString(R.string.key_output_format));
            key_output_format.setOnPreferenceChangeListener(outputFormatListener);

            key_show_notification = (SwitchPreference) findPreference(getString(R.string.key_show_notification));

            setPreviousSelectedAsSummary();

        }

        private void setPreviousSelectedAsSummary() {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String video_resolution = prefs.getString("key_video_resolution",null);
            boolean audio_enabled = prefs.getBoolean("key_record_audio", true);
            String audio_source = prefs.getString("key_audio_source", null);
            String video_encoder = prefs.getString("key_video_encoder", null);
            String video_frame_rate = prefs.getString("key_video_fps", null);
            String video_bit_rate = prefs.getString("key_video_bitrate",null);
            String output_format = prefs.getString("key_output_format",null);
            boolean show_notificaton = prefs.getBoolean("key_show_notification", false);

            /*Record Audio Prefs*/
            key_record_audio.setChecked(audio_enabled);

            /*Audio Source Prefs*/
            if (audio_source!=null){
                int index = key_audio_source.findIndexOfValue(audio_source);
                key_audio_source.setSummary(key_audio_source.getEntries()[index]);

            }else{
                String defaultSummary = PreferenceManager.getDefaultSharedPreferences(key_audio_source.getContext()).getString(key_audio_source.getKey(), "");
                key_audio_source.setSummary(defaultSummary);
            }

            /*Video Encoder Prefs*/
            if (video_encoder!=null){
                int index = key_video_encoder.findIndexOfValue(video_encoder);
                key_video_encoder.setSummary(key_video_encoder.getEntries()[index]);

            }else{
                String defaultSummary = PreferenceManager.getDefaultSharedPreferences(key_video_encoder.getContext()).getString(key_video_encoder.getKey(), "");
                key_video_encoder.setSummary(defaultSummary);
            }

            /*Video Resolution Prefs*/
            if (video_resolution!=null){
                int index = key_video_resolution.findIndexOfValue(video_resolution);
                key_video_resolution.setSummary(key_video_resolution.getEntries()[index]);

            }else{
                String defaultSummary = PreferenceManager.getDefaultSharedPreferences(key_video_resolution.getContext()).getString(key_video_resolution.getKey(), "");
                key_video_resolution.setSummary(defaultSummary);
            }

            /*Video Frame Rate Prefs*/
            if (video_frame_rate!=null){
                int index = key_video_fps.findIndexOfValue(video_frame_rate);
                key_video_fps.setSummary(key_video_fps.getEntries()[index]);

            }else{
                String defaultSummary = PreferenceManager.getDefaultSharedPreferences(key_video_fps.getContext()).getString(key_video_fps.getKey(), "");
                key_video_fps.setSummary(defaultSummary);
            }

            /*Video Bit Rate Prefs*/
            if (video_bit_rate!=null){
                int index = key_video_bitrate.findIndexOfValue(video_bit_rate);
                key_video_bitrate.setSummary(key_video_bitrate.getEntries()[index]);

            }else{
                String defaultSummary = PreferenceManager.getDefaultSharedPreferences(key_video_bitrate.getContext()).getString(key_video_bitrate.getKey(), "");
                key_video_bitrate.setSummary(defaultSummary);
            }

            /*Output Format Prefs*/
            if (output_format!=null){
                int index = key_output_format.findIndexOfValue(output_format);
                key_output_format.setSummary(key_output_format.getEntries()[index]);

            }else{
                String defaultSummary = PreferenceManager.getDefaultSharedPreferences(key_output_format.getContext()).getString(key_output_format.getKey(), "");
                key_output_format.setSummary(defaultSummary);
            }

            /*Notification Prefs*/
            key_show_notification.setChecked(show_notificaton);

        }

        /*Audio Source*/
        private Preference.OnPreferenceChangeListener audioSourceListener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String stringValue = newValue.toString();
                ListPreference listPreference = (ListPreference) findPreference(getString(R.string.key_audio_source));
                int index = listPreference.findIndexOfValue(stringValue);
                listPreference.setSummary(listPreference.getEntries()[index]);
                return true;
            }
        };

        /*Video Encoder*/
        private Preference.OnPreferenceChangeListener videoEncoderListener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String stringValue = newValue.toString();
                ListPreference listPreference = (ListPreference) findPreference(getString(R.string.key_video_encoder));
                int index = listPreference.findIndexOfValue(stringValue);
                listPreference.setSummary(listPreference.getEntries()[index]);
                listPreference.setValue(stringValue);
                return true;
            }
        };

        /*Video Resolution*/
        private Preference.OnPreferenceChangeListener videoResolutionListener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String stringValue = newValue.toString();
                ListPreference listPreference = (ListPreference) findPreference(getString(R.string.key_video_resolution));
                int index = listPreference.findIndexOfValue(stringValue);
                listPreference.setSummary(listPreference.getEntries()[index]);
                listPreference.setValue(stringValue);
                return true;
            }
        };

        /*Video Frame Rate*/
        private Preference.OnPreferenceChangeListener videoFrameRateListener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String stringValue = newValue.toString();
                ListPreference listPreference = (ListPreference) findPreference(getString(R.string.key_video_fps));
                int index = listPreference.findIndexOfValue(stringValue);
                listPreference.setSummary(listPreference.getEntries()[index]);
                listPreference.setValue(stringValue);
                return true;
            }
        };

        /*Video Bit Rate*/
        private Preference.OnPreferenceChangeListener videoBitRateListener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String stringValue = newValue.toString();
                ListPreference listPreference = (ListPreference) findPreference(getString(R.string.key_video_bitrate));
                int index = listPreference.findIndexOfValue(stringValue);
                listPreference.setSummary(listPreference.getEntries()[index]);
                listPreference.setValue(stringValue);
                return true;
            }
        };

        /*outputFormat*/
        private Preference.OnPreferenceChangeListener outputFormatListener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String stringValue = newValue.toString();
                ListPreference listPreference = (ListPreference) findPreference(getString(R.string.key_output_format));
                int index = listPreference.findIndexOfValue(stringValue);
                listPreference.setSummary(listPreference.getEntries()[index]);
                listPreference.setValue(stringValue);
                return true;
            }
        };


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
