package com.example.carlrygart.schoolfinder;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SchoolFinder is the main model class in the application. When created, it will fetch the latest
 * schools by using the SchoolDAO object.
 */
public class SchoolFinder {

    private final static String LOG_TAG = "SCHOOLFINDER";

    private SchoolDAO schoolDAO;
    private static List<School> schools;
    private static List<String> availablePrograms;

    public SchoolFinder() {
        schoolDAO = new SchoolDAO();
        fetchSchools();
    }

    public static List<School> getSchools() {
        return schools;
    }

    public static List<String> getAvailablePrograms() {
        return availablePrograms;
    }

    /**
     * Adds a provided program to to list of available programs.
     * @param program String with name of the program.
     */
    public static void addProgramToAvailablePrograms(String program) {
        availablePrograms.add(program);
    }

    /**
     * Fetches the schools from the SchoolDAO object and sorts the available programs list.
     */
    public void fetchSchools() {
        if (schools == null) {
            schoolDAO.execute();
            try {
                availablePrograms = new ArrayList<>();
                schools = schoolDAO.get();
                Log.d(LOG_TAG, "Got list of Schools!");
                Collections.sort(availablePrograms);
                Log.d("PROGRAM", "Number of programs: " + String.valueOf(availablePrograms.size()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.d(LOG_TAG, "Schools already filled!");
        }
    }

    /**
     * Extracts all the school names from the school objects.
     * @return List of all the school names.
     */
    public List<String> getSchoolStringList() {
        List<String> list = new ArrayList<>();
        if (schools == null) return list;
        for (School school: schools) {
            list.add(school.getName());
        }
        return list;
    }

    /**
     * Extracts a specific school object depending on its name.
     * @param name The schools name.
     * @return The requested school object.
     */
    public static School getSchoolByName(String name) {
        for (School school: schools) {
            if (school.getName().equals(name)) {
                return school;
            }
        }
        return null;
    }
}