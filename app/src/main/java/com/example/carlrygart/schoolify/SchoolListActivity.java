package com.example.carlrygart.schoolify;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Schools. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link SchoolDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class SchoolListActivity extends AppCompatActivity implements OnMapReadyCallback, FilterDialogFragment.FilterDialogListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private static final String LOG_TAG = "SCHOOLLIST";
    private boolean mTwoPane;
    private GoogleMap mMap;
    private RecyclerView recyclerView;
    private int chosenDistance;
    private List<String> selectedPrograms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        //selectedPrograms = Schoolify.availablePrograms;

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = (RecyclerView) findViewById(R.id.school_list);
        //assert recyclerView != null;
        //setupRecyclerView(recyclerView);
        List<String> tempProg = new ArrayList<>(Schoolify.availablePrograms);
        onFinishDialog(tempProg, 20);

        if (findViewById(R.id.school_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ImageView filterButton = (ImageView) findViewById(R.id.filter_button);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = FilterDialogFragment.newInstance(selectedPrograms, chosenDistance);
                newFragment.show(getFragmentManager(), "dialog");
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        for (School school : Schoolify.schools) {
            mMap.addMarker(new MarkerOptions().position(school.getLocation()).title(school.getName()));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(55.6, 13.0), 11));
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == android.R.id.home) {
//            // This ID represents the Home or Up button. In the case of this
//            // activity, the Up button is shown. Use NavUtils to allow users
//            // to navigate up one level in the application structure. For
//            // more details, see the Navigation pattern on Android Design:
//            //
//            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
//            //
//            //navigateUpFromSameTask(this);
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(Schoolify.schools));
    }

    @Override
    public void onFinishDialog(List<String> selectedPrograms, int chosenDistance) {
        this.selectedPrograms = selectedPrograms;
        this.chosenDistance = chosenDistance;
        List<School> schoolsToView = new ArrayList<>();
        for (School school: Schoolify.schools) {
            if (school.hasOneOfPrograms(selectedPrograms) && (school.isWithinDistance(chosenDistance) || chosenDistance == 20)) schoolsToView.add(school);
        }
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(schoolsToView));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<School> mValues;
        private View selectedView;

        public SimpleItemRecyclerViewAdapter(List<School> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.school_list_content, parent, false);
            //getWindow().setTitle("Skolväljaren");
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mSchoolNameView.setText(mValues.get(position).getName());

            Location userLoc = MainActivity.mLastLocation;
            LatLng schoolLoc = holder.mItem.getLocation();
            double distance = DistanceCalculator.calc(userLoc.getLatitude(), userLoc.getLongitude(), schoolLoc.latitude, schoolLoc.longitude, "K");
            String distanceText = String.format("%.2f km från din position", distance);
            holder.mDistance.setText(distanceText);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //int bgColor = ((ColorDrawable) view.getBackground()).getColor();
                    if (selectedView != null) selectedView.setBackgroundColor(Color.parseColor("#fff3f3f3"));
                    view.setBackgroundColor(Color.GRAY);
                    selectedView = view;

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(holder.mItem.getLocation()).zoom(13).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    //mMarker.showInfoWindow();
                }
            });

            holder.mInfoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(SchoolDetailFragment.ARG_ITEM_ID, holder.mItem.getName());
                        SchoolDetailFragment fragment = new SchoolDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.school_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, SchoolDetailActivity.class);
                        intent.putExtra(SchoolDetailFragment.ARG_ITEM_ID, holder.mItem.getName());
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            //public final TextView mIdView;
            public final TextView mSchoolNameView;
            public TextView mDistance;
            public final ImageView mInfoButton;
            public School mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                //mIdView = (TextView) view.findViewById(R.id.id);
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
