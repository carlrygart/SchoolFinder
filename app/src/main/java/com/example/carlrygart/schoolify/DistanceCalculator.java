package com.example.carlrygart.schoolify;

/**
 * Class to calculate the distance in km between two coordinates in latitude and longitude format.
 * The code is inspired of:
 * http://stackoverflow.com/questions/389211/geospatial-coordinates-and-distance-in-kilometers
 */
public class DistanceCalculator {

    /**
     * The main method of this tool. Calculates the distance between the provided coordinates in km.
     * @param lat1 Latitude coordinate 1.
     * @param lng1 Longitude coordinate 1.
     * @param lat2 Latitude coordinate 2.
     * @param lng2 Longitude coordinate 2.
     * @return Distance in km.
     */
    public static double calc(double lat1, double lng1, double lat2, double lng2) {
        double delta = lng1 - lng2;
        double distance =   Math.sin(deg2rad(lat1)) *
                            Math.sin(deg2rad(lat2)) +
                            Math.cos(deg2rad(lat1)) *
                            Math.cos(deg2rad(lat2)) *
                            Math.cos(deg2rad(delta));
        distance = Math.acos(distance);
        distance = (distance * 180 / Math.PI);
        distance = distance * 60 * 1.852;
        return (distance);
    }

    /**
     * Convert degrees to radians.
     * @param deg Double in degrees.
     * @return Double in radians.
     */
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
}
