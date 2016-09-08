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

/**
 * DAO for fetching all information regarding the schools. The code is inspired of
 * http://www.java2s.com/Open-Source/Android_Free_Code/Weather/tutorial/
 * com_example_androidtutorialsunshineForecastFragment_java.htm
 *
 * The class is using three different API's. One for fetching a list of all schools in Skåne,
 * one for fetching more specific details of each school, and the last are for fetching the location
 * of each school from Google's Places API. Each school, with its information is stored in the
 * entity class School.java.
 */
public class SchoolDAO extends AsyncTask<Void, Void, ArrayList<School>> {

    private String LOG_TAG = "SCHOOLDAO";

    /**
     * Asynchronous HTTP GET request for fetching the list of schools. The method is divided in
     * three parts, first the URL is prepared, then the HTTP request done, and lastly the data
     * gets parsed with JSON and the list of school is created.
     * The method is calling getLatLngFromAddress for each school entry to fetch the right
     * school information.
     * @param params
     * @return a list of schools
     */
    protected ArrayList<School> doInBackground(Void... params) {

        // Prepare and build the URL string.
        final String BASE_ADDR = "http://api.skanegy.se/get/schools/";

        // Make the HTTP request.
        String schoolListJsonStr = makeHTTPRequest(BASE_ADDR);

        // Create the JSON object, checks if the request was successful, and in that case
        // extracts the requested information.
        try {
            Log.d(LOG_TAG, schoolListJsonStr);
            Log.d(LOG_TAG, "-----------------------");
            int limit = 0;
            JSONObject resultJson = new JSONObject(schoolListJsonStr);
            JSONArray resultArray = resultJson.getJSONArray("result");
            ArrayList<School> listOfSchools = new ArrayList<>();
            for (int i = 0; i < resultArray.length(); ++i) {
                JSONObject object = resultArray.getJSONObject(i);
                int id = object.getInt("id");
                String name = object.getString("name");
                if (name.contains("Malmö")) {
                    School school = getSchoolSpecification(id);
                    listOfSchools.add(school);
                    limit++;
                }
                if (limit == 3) return listOfSchools;
            }
            Log.d(LOG_TAG, "Size of school array is " + listOfSchools.size());
            return listOfSchools;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * HTTP GET request for fetching the specification of a school. The method is divided in three parts,
     * first the URL is prepared, then the HTTP request done, and lastly the data gets parsed with
     * JSON. The method is calling getLatLngFromAddress for each school entry to fetch the right
     * location.
     * @param schoolId Integer with the requested school id, used in the URL.
     * @return A school object with all parameters.
     */
    private School getSchoolSpecification(int schoolId) {

        // Prepare and build the URL string.
        final String BASE_ADDR = "http://api.skanegy.se/get/schools/" + schoolId;

        // Make the HTTP request.
        String schoolSpecJsonStr = makeHTTPRequest(BASE_ADDR);

        // Create the JSON object, checks if the request was successful, and in that case
        // extracts the requested information.
        try {
            Log.d(LOG_TAG, schoolSpecJsonStr);
            JSONObject resultJson = new JSONObject(schoolSpecJsonStr);
            JSONObject resultObject = resultJson.getJSONObject("result");
            JSONObject addressObject = resultObject.getJSONArray("addresses").getJSONObject(0);
            JSONArray programsArray = resultObject.getJSONArray("programs");
            int id = resultObject.getInt("id");
            String name = resultObject.getString("name");
            String address = addressObject.getString("address1");
            if (address.contains("Postadress:")) address = address.substring(12);
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
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Method for fetching the location information from Google Places API. The method contains
     * three parts, where the first is preparing the URL, second is fetching the information
     * from the API, and the last is parsing the information and create the object.
     * @param address String describing the address of the school, e.g. Skolgatan 3, Stockholm.
     * @return The location of the entered address in a LatLng object.
     */
    public LatLng getLatLngFromAddress(String address) {

        // Prepare and build the URL string.
        final String BASE_ADDR = "https://maps.googleapis.com/maps/api/place/textsearch/json?";
        final String ADDRESS_PARAM = "query";
        final String KEY_PARAM = "key";
        final String KEY_VALUE = "AIzaSyAZSKPTTrRyl7NT7HUsQcMYb5AzTC-Q-h4";
        Uri builtUri = Uri.parse(BASE_ADDR).buildUpon()
                .appendQueryParameter(ADDRESS_PARAM, address)
                .appendQueryParameter(KEY_PARAM, KEY_VALUE)
                .build();

        // Make the HTTP request.
        String locationJsonStr = makeHTTPRequest(builtUri.toString());

        // Create the JSON object, checks if the request was successful, and in that case
        // extracts the requested information.
        try {
            JSONObject locationJson = new JSONObject(locationJsonStr);
            if (locationJson.getString("status").equals("OK")) {
                JSONArray locationArray = locationJson.getJSONArray("results");
                double[] result = new double[2];
                JSONObject Object = locationArray.getJSONObject(0);
                JSONObject geometry = Object.getJSONObject("geometry");
                JSONObject locationObject = geometry.getJSONObject("location");
                result[0] = locationObject.getDouble("lat");
                result[1] = locationObject.getDouble("lng");
                Log.d(LOG_TAG, "Location is " + result[0] + ", " + result[1]);
                Log.d(LOG_TAG, "-----------------------");
                return new LatLng(result[0], result[1]);
            } else {
                Log.d(LOG_TAG, "The location could not be found. Google API status: " + locationJson.getString("status") + ". Setting (55.6, 13.0)");
                Log.d(LOG_TAG, "-----------------------");
                return new LatLng(55.6, 13);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * General method for fetching data with a HTTP GET request.
     * @param urlStr The requested URL as a String.
     * @return A JSON string, ready to parse.
     */
    private String makeHTTPRequest(String urlStr) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Prepare the URL.
        URL url = null;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // Execute the request.
        try {
            // Initiate the connection.
            urlConnection = null;
            if (url != null) {
                urlConnection = (HttpURLConnection) url.openConnection();
            }

            // Read the input stream into a string.
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                return null;
            }

            // Return resulting JSON string.
            return buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Some error in the HTTP request", e);
            return null;
        } finally {
            // Disconnect and close reader to release system resources.
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing buffered reader", e);
                }
            }
        }
    }
}
