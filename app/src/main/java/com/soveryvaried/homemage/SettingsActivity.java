package com.soveryvaried.homemage;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Ian on 8/2/2014.
 */
public class SettingsActivity  extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();


    }


}
