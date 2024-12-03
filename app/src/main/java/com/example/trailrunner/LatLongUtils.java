package com.example.trailrunner;

import com.google.android.gms.maps.model.LatLng;

/**
 * Utils class for dealing with calculating distances between two LatLng objects
 */
public class LatLongUtils {

    //Uses the Haversine Formula(calculate distance between two points on a sphere)
    //Slightly modified from https://www.geeksforgeeks.org/haversine-formula-to-find-distance-between-two-points-on-a-sphere/
    public static double calculateDistanceKm(LatLng point1, LatLng point2){
        // distance between latitudes and longitudes
        double distanceLatitude = Math.toRadians(point2.latitude - point1.latitude);
        double distanceLongitude = Math.toRadians(point2.longitude - point2.longitude);

        // convert to radians
        double latitude1Radians = Math.toRadians(point1.latitude);
        double latitude2Radians = Math.toRadians(point2.latitude);

        // apply formulae
        double a = Math.pow(Math.sin(distanceLatitude / 2), 2) +
                Math.pow(Math.sin(distanceLongitude / 2), 2) *
                        Math.cos(latitude1Radians) *
                        Math.cos(latitude2Radians);
        double radiusEarth = 6371;
        double c = 2 * Math.asin(Math.sqrt(a));
        return radiusEarth * c;
    }

    /**
     * Converts km to miles
     * @param kilometers
     * @return
     */
    public static double convertKmToMiles(double kilometers){
        return kilometers * 0.6213711922;
    }

    /**
     * Converts miles to km
     * @param miles
     * @return
     */
    public static double convertMilesToKm(double miles){
        return miles / 0.6213711922;
    }
}
