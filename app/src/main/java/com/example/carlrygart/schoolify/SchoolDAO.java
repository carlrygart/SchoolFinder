package com.example.carlrygart.schoolify;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SchoolDAO extends AsyncTask<Void, Void, ArrayList<School>> {

    private String LOG_TAG = "SCHOOLDAO";
    private HttpURLConnection urlConnection = null;
    private BufferedReader reader = null;
    private String JsonStr = null;

    private int limit;

    protected ArrayList<School> doInBackground(Void... params) {
        try {
            final String BASE_ADDR = "http://api.skanegy.se/get/schools/";

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
            return getSchoolsFromJson(JsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<School> getSchoolsFromJson(String JsonStr)
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
                if (limit == 6) return listOfSchools;
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
            //Log.d(LOG_TAG, "Ready to parse!");
            return getSchoolSpecFromJson(JsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
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
        String facebook = (resultObject.getString("socialMediaType").equals("facebook")) ? resultObject.getString("socialMediaLink") : null;
        LatLng location = getLatLngFromAddress(address + ", " + postalCode); // new LatLng(55.6, 13.0);
        List<String> offeredPrograms = new ArrayList<>();
        for (int i = 0; i < programsArray.length(); ++i) {
            String program = programsArray.getJSONObject(i).getString("name");
            if (!offeredPrograms.contains(program)) offeredPrograms.add(program);
            if (!Schoolify.availablePrograms.contains(program)) Schoolify.availablePrograms.add(program);
        }
        return new School(id, name, address, postalCode, city, website, phone, email, facebook, location, offeredPrograms);
    }

    public LatLng getLatLngFromAddress(String address) {
        final String BASE_ADDR = "https://maps.googleapis.com/maps/api/place/textsearch/json?";
        final String ADDRESS_PARAM = "query";
        final String KEY_PARAM = "key";
        final String KEY_VALUE = "AIzaSyAZSKPTTrRyl7NT7HUsQcMYb5AzTC-Q-h4";
        Uri builtUri = Uri.parse(BASE_ADDR).buildUpon()
                .appendQueryParameter(ADDRESS_PARAM, address)
                .appendQueryParameter(KEY_PARAM, KEY_VALUE)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

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
            Log.e(LOG_TAG, "Error ", e);
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
            return getLatLngFromJson(JsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    private LatLng getLatLngFromJson(String JsonStr) throws JSONException {
        //Log.d(LOG_TAG, JsonStr);
        // These are the names of the JSON objects that need to be extracted.
        final String RESULTS = "results";
        final String GEOMETRY = "geometry";
        final String LOCATION = "location";

        JSONObject locationJson = new JSONObject(JsonStr);
        if (locationJson.getString("status").equals("OK")) {
            JSONArray locationArray = locationJson.getJSONArray(RESULTS);

            double[] result = new double[2];
            JSONObject Object = locationArray.getJSONObject(0);
            JSONObject geometry = Object.getJSONObject(GEOMETRY);
            JSONObject locationObject = geometry.getJSONObject(LOCATION);
            result[0] = locationObject.getDouble("lat");
            result[1] = locationObject.getDouble("lng");
            Log.d(LOG_TAG, "Location is " + result[0] + ", " + result[1]);
            Log.d(LOG_TAG, "-----------------------");
            return new LatLng(result[0], result[1]);
        } else {
            Log.d(LOG_TAG, "In else: Status is " + locationJson.getString("status"));
            return null;
        }
    }
}
