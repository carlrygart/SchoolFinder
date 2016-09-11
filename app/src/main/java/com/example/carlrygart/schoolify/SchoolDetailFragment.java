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
 * A fragment representing a single School's detail screen. It's only used by SchoolDetailActivity
 * for now, but can be used by SchoolListActivity (when using a tablet) in the future.
 */
public class SchoolDetailFragment extends Fragment {

    public static final String ARG_SCHOOL_NAME = "school_name";

    // The content (the school's different parameters) of the view.
    private School mSchool;
    private TextView mSchoolTitle;
    private TextView mSchoolPrograms;
    private TextView mSchoolWebsite;
    private TextView mSchoolPhone;
    private TextView mSchoolEmail;
    private TextView mSchoolFacebook;
    private TextView mSchoolAddress;

    public SchoolDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_SCHOOL_NAME)) {
            Activity activity = this.getActivity();

            // Fetch the school object from the model.
            mSchool = Schoolify.getSchoolByName(getArguments().getString(ARG_SCHOOL_NAME));

            // Add the school's name to the title.
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mSchool.getName());
            }
        }
    }

    /**
     * Method to fill the TextViews with content.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Use the school_detail layout.
        View rootView = inflater.inflate(R.layout.school_detail, container, false);

        // Find all views.
        mSchoolTitle = (TextView) rootView.findViewById(R.id.school_title);
        mSchoolPrograms = (TextView) rootView.findViewById(R.id.school_programs);
        mSchoolWebsite = (TextView) rootView.findViewById(R.id.school_website);
        mSchoolPhone = (TextView) rootView.findViewById(R.id.school_phone);
        mSchoolEmail = (TextView) rootView.findViewById(R.id.school_email);
        mSchoolAddress = (TextView) rootView.findViewById(R.id.school_address);
        mSchoolFacebook = (TextView) rootView.findViewById(R.id.school_facebook);

        // Fill all views with content.
        if (mSchool != null) {
            mSchoolTitle.setText(mSchool.getName());
            String programText = "";
            for (String pro: mSchool.getPrograms()) {
                programText += pro + "\n";
            }
            mSchoolPrograms.setText(programText);
            mSchoolWebsite.setText(mSchool.getWebSite());
            mSchoolPhone.setText(mSchool.getPhone());
            mSchoolEmail.setText(mSchool.getEmail());
            String addressText = mSchool.getAddress() +"\n"+ mSchool.getPostalCode() +" "+ mSchool.getCity();
            mSchoolAddress.setText(addressText);
            mSchoolFacebook.setText(mSchool.getFacebook());
        }
        return rootView;
    }
}
