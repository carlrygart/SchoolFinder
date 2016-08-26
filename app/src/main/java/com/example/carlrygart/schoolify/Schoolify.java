package com.example.carlrygart.schoolify;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Schoolify {

    final static String LOG_TAG = "SCHOOLIFY";

    private SchoolDAO schoolDAO;
    public static List<School> schools;
    public static List<String> availablePrograms;

    public Schoolify() {
        schoolDAO = new SchoolDAO();
        fetchSchools();
    }

    public void fetchSchools() {
        if (schools == null) {
            schoolDAO.execute();
            try {
                availablePrograms = new ArrayList<>();
                schools = schoolDAO.get();
                Log.d(LOG_TAG, "Got list of Schools!");
                Collections.sort(availablePrograms);
                //Log.d("PROGRAM", availablePrograms.toString());
                Log.d("PROGRAM", "Number of programs: " + String.valueOf(availablePrograms.size()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(LOG_TAG, "Schools already filled!");
            Log.d(LOG_TAG, schools.size() + "First obj: " + schools.get(0).getName());
        }
    }

    public List<School> getSchools() {
        return schools;
    }

    public List<String> getSchoolStringArray() {
        List<String> list = new ArrayList<>();
        if (schools == null) return list;
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