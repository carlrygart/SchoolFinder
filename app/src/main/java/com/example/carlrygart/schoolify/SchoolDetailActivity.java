package com.example.carlrygart.schoolify;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;

/**
 * An activity representing a single School's details screen. It's using the SchoolDetailFragment
 * to fill the content of the school. This is because the application is prepared to be further
 * developed to view the application in two-pane view on an tablet. However, for now the application
 * is only optimized for handsets.
 */
public class SchoolDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Adds the listener for the floating red map button that will start the full screen
        // map activity.
        FloatingActionButton mapButton = (FloatingActionButton) findViewById(R.id.map_button);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SchoolDetailActivity.this, MapsActivity.class);
                intent.putExtra("school_name", getIntent().getStringExtra(SchoolDetailFragment.ARG_SCHOOL_NAME));
                startActivity(intent);
            }
        });

        // Add the fragment if there is no savedInstanceState (for example when rotating screen).
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(SchoolDetailFragment.ARG_SCHOOL_NAME,
                    getIntent().getStringExtra(SchoolDetailFragment.ARG_SCHOOL_NAME));
            SchoolDetailFragment fragment = new SchoolDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.school_detail_container, fragment)
                    .commit();
        }
    }

    /**
     * Used to handle the back button in actionbar, so when it is pressed the application
     * will go back to the same activity state as before.
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== android.R.id.home) {
            Intent intent = NavUtils.getParentActivityIntent(this);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            NavUtils.navigateUpTo(this, intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
