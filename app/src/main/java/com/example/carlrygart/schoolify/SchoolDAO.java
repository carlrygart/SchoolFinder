package com.example.carlrygart.schoolify;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SchoolDAO extends AsyncTask<Void, Void, ArrayList<School>> {

    private String LOG_TAG = "SCHOOLDAO";
    private HttpURLConnection urlConnection = null;
    private BufferedReader reader = null;
    private String JsonStr = null;

    private int limit;

    protected ArrayList<School> doInBackground(Void... params) {
        try {
            final String BASE_ADDR = "http://api.skanegy.se/get/schools/";
//            final String KEY_PARAM = "resource_id";
//            final String KEY_VALUE = "de6fbf16-9e05-495d-9371-8b706bba5be2";
//            final String LIMIT_PARAM = "limit";
//            final String LIMIT_VALUE = "60";
//
//            Uri builtUri = Uri.parse(BASE_ADDR).buildUpon()
//                    .appendQueryParameter(KEY_PARAM, KEY_VALUE)
//                    .appendQueryParameter(LIMIT_PARAM, LIMIT_VALUE)
//                    .build();

            URL url = new URL(BASE_ADDR);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            JsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Exception ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        try {
            Log.d(LOG_TAG, "Ready to parse!");
            return getSchoolDataFromJson(JsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }

    private ArrayList<School> getSchoolDataFromJson(String JsonStr)
            throws JSONException {
        Log.d(LOG_TAG, JsonStr);
        limit = 0;
        // These are the names of the JSON objects that need to be extracted.
        final String RESULT = "result";
        JSONObject resultJson = new JSONObject(JsonStr);
        JSONArray resultArray = resultJson.getJSONArray(RESULT);
        ArrayList<School> listOfSchools = new ArrayList<>();
        for (int i = 0; i < resultArray.length(); ++i) {
            JSONObject object = resultArray.getJSONObject(i);
            int id = object.getInt("id");
            String name = object.getString("name");
            if (name.contains("Malmö")) {
                if (limit == 5) return listOfSchools;
                School school = getSchoolSpecifications(id);
                listOfSchools.add(school);
                limit++;
                //Log.d(LOG_TAG, "School " + i + " added: ID " + school.getId() + " - " + school.getName() + " - " + school.getAddress());
            }
        }
        Log.d(LOG_TAG, "Size of school array is " + listOfSchools.size());
        return listOfSchools;
    }

    private School getSchoolSpecifications(int schoolId) {
        try {
            final String BASE_ADDR = "http://api.skanegy.se/get/schools/" + schoolId;

            URL url = new URL(BASE_ADDR);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            JsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Exception ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        try {
            Log.d(LOG_TAG, "Ready to parse!");
            return getSchoolSpecFromJson(JsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }

    private School getSchoolSpecFromJson(String JsonStr)
            throws JSONException {
        Log.d(LOG_TAG, JsonStr);
        // These are the names of the JSON objects that need to be extracted.
        final String RESULT = "result";
        final String ADDRESSES = "addresses";
        final String PROGRAMS = "programs";
        JSONObject resultJson = new JSONObject(JsonStr);
        JSONObject resultObject = resultJson.getJSONObject(RESULT);
        JSONObject addressObject = resultObject.getJSONArray(ADDRESSES).getJSONObject(0);
        JSONArray programsArray = resultObject.getJSONArray(PROGRAMS);
        int id = resultObject.getInt("id");
        String name = resultObject.getString("name");
        String address = addressObject.getString("address1");
        String postalCode = addressObject.getString("postalCode");
        String city = addressObject.getString("city");
        String phone = resultObject.getString("phone");
        String email = resultObject.getString("email");
        String website = resultObject.getString("website");
        List<String> programs = new ArrayList<>();
        for (int i = 0; i < programsArray.length(); ++i) {
            JSONObject object = programsArray.getJSONObject(i);
            programs.add(object.getString("name"));
        }
        return new School(id, name, address, postalCode, city, phone, email, website, programs);
    }
}
