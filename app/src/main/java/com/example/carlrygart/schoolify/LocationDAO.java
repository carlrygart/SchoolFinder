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

public class LocationDAO extends AsyncTask<String, Void, double[]> {

    private String LOG_TAG = "LOCATIONDAO";
    private String address;

    @Override
    protected double[] doInBackground(String... strings) {
//        Log.d(LOG_TAG,"Must be concat"+strings[0]);
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String JsonStr = null;
        address = strings[0];

        try {
            final String BASE_ADDR = "https://maps.googleapis.com/maps/api/place/textsearch/json?";
            final String ADDRESS_PARAM = "query";
            final String KEY_PARAM = "key";
            final String KEY_VALUE = "AIzaSyAZSKPTTrRyl7NT7HUsQcMYb5AzTC-Q-h4";
            Log.d(LOG_TAG, address);
            Uri builtUri = Uri.parse(BASE_ADDR).buildUpon()
                    .appendQueryParameter(ADDRESS_PARAM, address)
                    .appendQueryParameter(KEY_PARAM, KEY_VALUE)
                    .build();
            Log.d(LOG_TAG,builtUri.toString());
            URL url = new URL(builtUri.toString());
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
            Log.e(LOG_TAG, "Error ", e);
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
//            Log.d(LOG_TAG,"Ready to parse!");
            return getLocationDataFromJson(JsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }


    private double[] getLocationDataFromJson(String JsonStr)
            throws JSONException {
        Log.d(LOG_TAG, JsonStr);
        // These are the names of the JSON objects that need to be extracted.
        final String OWM_RESULTS = "results";
        final String OWM_GEOMETRY = "geometry";
        final String OWM_LOCATION = "location";
        final String OWM_STATUS = "status";

        JSONObject locationJson = new JSONObject(JsonStr);
        if (locationJson.getString("status").equals("OK")) {
            JSONArray locationArray = locationJson.getJSONArray(OWM_RESULTS);

            double[] result = new double[2];
            JSONObject Object = locationArray.getJSONObject(0);
            JSONObject geometry = Object.getJSONObject(OWM_GEOMETRY);
            JSONObject locationObject = geometry.getJSONObject(OWM_LOCATION);
            result[0] = locationObject.getDouble("lat");
            result[1] = locationObject.getDouble("lng");
            Log.d(LOG_TAG, "Location data is " + result[0] + "," + result[1]);
            return result;
        } else {
            Log.d(LOG_TAG, "In else:Status is " + locationJson.getString("status"));
            return null;
        }
    }
}
