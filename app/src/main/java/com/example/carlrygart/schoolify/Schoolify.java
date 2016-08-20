package com.example.carlrygart.schoolify;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Schoolify {

    final static String LOG_TAG = "SCHOOLIFY";

    private SchoolifyDbHelper mDbHelper;
    private SchoolDAO schoolDAO;
    private static ArrayList<School> schools;

    public Schoolify() {
        mDbHelper = new SchoolifyDbHelper(MainActivity.appContext);
        schoolDAO = new SchoolDAO();
        fetchSchools();
    }

    public void fetchSchools() {
        if (schools == null) {
            schoolDAO.execute();
            try {
                schools = schoolDAO.get();
                Log.d(LOG_TAG, "List of Schools got!");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else {

        }
    }

    public ArrayList<School> getSchools() {
        return schools;
    }

    public List<String> getSchoolStringArray() {
        List<String> list = new ArrayList<>();
        for (School school: schools) {
            list.add(school.getName());
        }
        return list;
    }

    public static School getSchoolByName(String name) {
        for (School school: schools) {
            if (school.getName().equals(name)) {
                return school;
            }
        }
        return null;
    }
}
