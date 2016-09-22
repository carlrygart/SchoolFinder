package com.example.carlrygart.schoolfinder;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Entity class representing the object school. Keeps all necessary information about the school.
 */
public class School {

    private int id;
    private String name;
    private String address;
    private String postalCode;
    private String city;
    private String webSite;
    private String phone;
    private String email;
    private String facebook;
    private LatLng location;
    private List<String> programs;

    public School(int id, String name, String address, String postalCode, String city,
                  String webSite, String phone, String email, String facebook,
                  LatLng location, List<String> programs) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.city = city;
        this.webSite = webSite;
        this.phone = phone;
        this.email = email;
        this.facebook = facebook;
        this.location = location;
        this.programs = programs;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCity() {
        return city;
    }

    public String getWebSite() {
        return webSite;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getFacebook() {
        return facebook;
    }

    public LatLng getLocation() {
        return location;
    }

    public List<String> getPrograms() {
        return programs;
    }

    /**
     * Method for checking if the school is offering one of a list of programs.
     * @param chosenPrograms List of programs.
     * @return True or false depending on result of check.
     */
    public boolean hasOneOfPrograms(List<String> chosenPrograms) {
        for (String pro: chosenPrograms) {
            if (programs.contains(pro)) return true;
        }
        return false;
    }

    /**
     * Method for checking whether a school are within distance of chosen distance for the
     * provided user location.
     * @param userLoc The current location of the user.
     * @param acceptableDistance The chosen distance.
     * @return True or false depending on result of check.
     */
    public boolean isWithinDistance(Location userLoc, int acceptableDistance) {
        if (userLoc == null) return true;
        double distance = DistanceCalculator.calc(userLoc.getLatitude(), userLoc.getLongitude(),
                location.latitude, location.longitude);
        //Log.d("ACCDISTANCE", String.valueOf(acceptableDistance));
        //Log.d("ACTUALDISTANCE", String.valueOf(distance));
        if (distance > acceptableDistance) return false;
        return true;
    }
}
