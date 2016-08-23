package com.example.carlrygart.schoolify;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A fragment representing a single School detail screen.
 * This fragment is either contained in a {@link SchoolListActivity}
 * in two-pane mode (on tablets) or a {@link SchoolDetailActivity}
 * on handsets.
 */
public class SchoolDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private TextView mSchoolAddress;
    private TextView mSchoolFacebook;
    private School mSchool;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SchoolDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {

            Activity activity = this.getActivity();

            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mSchool = Schoolify.getSchoolByName(getArguments().getString(ARG_ITEM_ID));

            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mSchool.getName());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.school_detail, container, false);

        mSchoolAddress = (TextView) rootView.findViewById(R.id.school_address);
        mSchoolFacebook = (TextView) rootView.findViewById(R.id.school_facebook);

        // Show the dummy content as text in a TextView.
        if (mSchool != null) {
            mSchoolAddress.setText(mSchool.getAddress());
            mSchoolFacebook.setText(mSchool.getFacebook());
        }

        return rootView;
    }
}
