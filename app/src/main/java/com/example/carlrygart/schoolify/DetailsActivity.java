package com.example.carlrygart.schoolify;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

    TextView schoolAddress;
    TextView schoolFacebook;
    School school;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton mapButton = (FloatingActionButton) findViewById(R.id.map_button);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailsActivity.this, MapsActivity.class);
                intent.putExtra("school_name", school.getName());
                startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        schoolAddress = (TextView) findViewById(R.id.school_address);
        schoolFacebook = (TextView) findViewById(R.id.school_facebook);

        school = Schoolify.getSchoolByName(intent.getStringExtra("school_name"));
        schoolAddress.setText(school.getAddress());
        schoolFacebook.setText(school.getFacebook());
        setTitle(school.getName());
    }
}
