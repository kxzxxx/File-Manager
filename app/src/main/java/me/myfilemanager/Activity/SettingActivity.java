package me.myfilemanager.Activity;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import me.myfilemanager.R;

public class SettingActivity extends AppCompatActivity {

    String TAG = SettingActivity.class.getSimpleName();

    Toolbar ab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.SettingActivity);
        ab = (Toolbar) findViewById(R.id.setting_toolbar);

        setSupportActionBar(ab);
        getFragmentManager().beginTransaction().
                replace(R.id.content_frame, new SettingPreference())
                .commit();
    }
    // This ID represents the Home or Up button. In the case of this
    // activity, the Up button is shown. Use NavUtils to allow users
    // to navigate up one level in the application structure. For
    // more details, see the Navigation pattern on Android Design:
    //
    // http://developer.android.com/design/patterns/navigation.html#up-vs-back
    //
    // TODO: If Settings has multiple levels, Up should navigate up
    // that hierarchy.


    /**
     * Shows the simplified settings UI if the device configuration if the
     * device configuration dictates that activity_self_edit simplified, single-pane UI should be
     * shown.
     */

	public static class SettingPreference extends PreferenceFragment {
	public void onCreate(final Bundle savedInstanceState){
	super.onCreate(savedInstanceState);
	addPreferencesFromResource(R.xml.settings);
	}
	}
}
