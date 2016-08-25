package com.example.carlrygart.schoolify;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Schoolify {

    final static String LOG_TAG = "SCHOOLIFY";

    private SchoolifyDbHelper mDbHelper;
    private SchoolDAO schoolDAO;
    public static List<School> schools;
    public static List<String> availablePrograms;

    public Schoolify() {
        mDbHelper = new SchoolifyDbHelper(MainActivity.appContext);
        schoolDAO = new SchoolDAO();
        availablePrograms = new ArrayList<>();
        fetchSchools();
    }

    public void fetchSchools() {
        if (schools == null) {
            schoolDAO.execute();
            try {
                schools = schoolDAO.get();
                Log.d(LOG_TAG, "Got list of Schools!");

//                Comparator<String> byName = new Comparator<String>() {
//                    @Override
//                    public int compare(String o1, String o2) {
//                        return o1.compareTo(o2);
//                    }
//                };
                Collections.sort(availablePrograms);
                Log.d("PROGRAM", availablePrograms.toString());
                Log.d("PROGRAM", String.valueOf(availablePrograms.size()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(LOG_TAG, "Schools already filled!");
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