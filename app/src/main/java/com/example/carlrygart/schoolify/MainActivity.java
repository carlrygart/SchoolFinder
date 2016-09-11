package com.example.carlrygart.schoolify;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Main activity class that initiates the application Schoolify and is the first thing to show
 * when starting the application.
 */
public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // This activity's log tag for problem shooting.
    public static String LOG_TAG = "MAINACTIVITY";

    // The model.
    private Schoolify schoolify;

    // This activity's view.
    private Spinner spinner;
    private Button findSchoolsButton, submitSpinnerButton;
    private ArrayAdapter<String> adapter;

    // Used for fetching the current location.
    public static GoogleApiClient mGoogleApiClient;
    public static Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Creates an instance of GoogleAPIClient to be able to get the current location of user.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        // Create an instance of the model Schoolify.
        schoolify = new Schoolify();

        // Spinner code. Fetching the array with schools. Creates the
        // spinner object and the adapter. Finally putting the adapter in the spinner object.
        spinner = (Spinner) findViewById(R.id.spinner);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, schoolify.getSchoolStringList());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Button for submitting the users choice in the spinner.
        submitSpinnerButton = (Button) findViewById(R.id.submit_spinner);
        submitSpinnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String spinnerValue = spinner.getSelectedItem().toString();
                Intent intent = new Intent(MainActivity.this, SchoolDetailActivity.class);
                intent.putExtra(SchoolDetailFragment.ARG_SCHOOL_NAME, spinnerValue);
                startActivity(intent);
            }
        });

        // Button for going to "Skolväljaren".
        findSchoolsButton = (Button) findViewById(R.id.find_schools_button);
        findSchoolsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SchoolListActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Inflates the menu containing e.g. "Om Schoolify".
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Handles the actionbar menu items.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Handles click on the menu choice "Om Schoolify".
        if (id == R.id.action_about) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Om Schoolify");
            alertDialog.setMessage(getResources().getString(R.string.about_schoolify_info));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.close),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    /**
     * Checks if the application got the right access' and sets the mLastLocation.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG_TAG, "No access to ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION");
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        //Log.d("LOCATION", mLastLocation.toString());
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(LOG_TAG, "Connection of GoogleApiClient failed!");
    }
}