package edu.uw.ztianai.yama;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Tianai Zhao on 16/04/23.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //action bar "back"
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}