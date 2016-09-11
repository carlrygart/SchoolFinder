package com.example.carlrygart.schoolify;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import android.support.v7.app.ActionBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity showing a list of Schools together with a map pinning all the school.
 * This activity has future opportunities to be developed showing different presentations
 * for handset and tablet-size devices. But for now it's just optimized for handsets due
 * to missing testing tablet.
 */
public class SchoolListActivity extends AppCompatActivity implements OnMapReadyCallback,
        FilterDialogFragment.FilterDialogListener {

    private static final String LOG_TAG = "SCHOOLLIST";

    private int chosenDistance;
    private List<String> selectedPrograms;
    private List<School> schoolsToView;

    private GoogleMap mMap;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Update the recycler view (list of schools) with either saved instance parameters or
        // with newly created.
        recyclerView = (RecyclerView) findViewById(R.id.school_list);
        if (savedInstanceState != null) {
            chosenDistance = savedInstanceState.getInt("chosenDistance");
            selectedPrograms = savedInstanceState.getStringArrayList("selectedPrograms");
            Log.d(LOG_TAG, "Restored state - Chosen distance: " + chosenDistance +
                    " and size of selected programs: " + selectedPrograms.size());
        } else {
            chosenDistance = 20;
            selectedPrograms = new ArrayList<>(Schoolify.getAvailablePrograms());
            Log.d(LOG_TAG, "Default config for recycler view.");
        }
        updateRecyclerViewAndMap();

        // Bind listener to filter button (the cog). The static newInstance method is used to be
        // able to send arguments.
        ImageView filterButton = (ImageView) findViewById(R.id.filter_button);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = FilterDialogFragment.newInstance(selectedPrograms, chosenDistance);
                newFragment.show(getFragmentManager(), "dialog");
            }
        });
    }

    /**
     * Save the the two instance parameters (chosenDistance, selectedPrograms) used to calculate
     * which schools to show in the RecyclerView.
     * @param savedInstanceState The saved instance.
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current state of filter.
        savedInstanceState.putInt("chosenDistance", chosenDistance);
        savedInstanceState.putStringArrayList("selectedPrograms", (ArrayList<String>) selectedPrograms);

        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Content that needs to be loaded after the map is ready, i.e. the pins of the schools and
     * and enable my location.
     * @param googleMap The provided map.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Enables my location if it's allowed.
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        // Adds the pins of each school to the map.
        for (School school : schoolsToView) {
            mMap.addMarker(new MarkerOptions().position(school.getLocation()).title(school.getName()));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(55.6, 13.0), 11));
    }

    /**
     * Method used by FilterDialogFragment when the user is closing the dialog.
     * @param selectedPrograms Chosen programs in the dialog.
     * @param chosenDistance Chosen distance in the dialog.
     */
    @Override
    public void onFinishDialog(List<String> selectedPrograms, int chosenDistance) {
        this.selectedPrograms = selectedPrograms;
        this.chosenDistance = chosenDistance;
        updateRecyclerViewAndMap();
    }

    /**
     * Method to update the adapter view with the wanted schools depending the offered programs
     * and distance from school.
     */
    private void updateRecyclerViewAndMap() {
        // Check if the school offers at least one program of the chosen and if it's within distance.
        schoolsToView = new ArrayList<>();
        for (School school: Schoolify.getSchools()) {
            if (school.hasOneOfPrograms(selectedPrograms) && (school.isWithinDistance(MainActivity.getLastLocation(), chosenDistance) || chosenDistance == 20)) schoolsToView.add(school);
        }

        // Sets adapter and updates the map with new pins.
        recyclerView.setAdapter(new SimpleSchoolRecyclerViewAdapter(schoolsToView));
        if (mMap != null) {
            mMap.clear();
            for (School school: schoolsToView) {
                mMap.addMarker(new MarkerOptions().position(school.getLocation()).title(school.getName()));
            }
        }
    }

    /**
     * Class used for binding the items (School) to holders in the RecyclerView.
     */
    public class SimpleSchoolRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleSchoolRecyclerViewAdapter.SchoolViewHolder> {

        private final List<School> mValues;
        private View selectedView;

        public SimpleSchoolRecyclerViewAdapter(List<School> items) {
            mValues = items;
        }

        /**
         * Inflates the right layout.
         */
        @Override
        public SchoolViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.school_list_content, parent, false);
            return new SchoolViewHolder(view);
        }

        /**
         * Method setting up the features for each holder in the school list.
         */
        @Override
        public void onBindViewHolder(final SchoolViewHolder holder, int position) {
            holder.mSchool = mValues.get(position);
            holder.mSchoolNameView.setText(holder.mSchool.getName());

            // Fetches the latest known location, the schools location, calculates and print out the distance.
            Location userLoc = MainActivity.getLastLocation();
            LatLng schoolLoc = holder.mSchool.getLocation();
            double distance = DistanceCalculator.calc(userLoc.getLatitude(), userLoc.getLongitude(), schoolLoc.latitude, schoolLoc.longitude);
            String distanceText = String.format("%.2f km från din position", distance);
            holder.mDistance.setText(distanceText);

            // Sets listener for if the user clicks on the holder.
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Sets background color depending if the holder already is selected or not.
                    if (selectedView != null) selectedView.setBackgroundColor(Color.parseColor("#fff3f3f3"));
                    view.setBackgroundColor(Color.GRAY);
                    selectedView = view;

                    // Centers the selected school on the map.
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(holder.mSchool.getLocation()).zoom(13).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            });

            // Sets listener for the info button.
            holder.mInfoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, SchoolDetailActivity.class);
                    intent.putExtra(SchoolDetailFragment.ARG_SCHOOL_NAME, holder.mSchool.getName());
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        /**
         * Nested class for each school in the RecyclerView.
         */
        public class SchoolViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public School mSchool;
            public final TextView mSchoolNameView;
            public TextView mDistance;
            public final ImageView mInfoButton;

            public SchoolViewHolder(View view) {
                super(view);
                mView = view;
                mSchoolNameView = (TextView) view.findViewById(R.id.school_name);
                mDistance = (TextView) view.findViewById(R.id.distance_to_user);
                mInfoButton = (ImageView) view.findViewById(R.id.info_button);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mSchoolNameView.getText() + "'";
            }
        }
    }
}
