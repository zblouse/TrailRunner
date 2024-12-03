package com.example.trailrunner;

import android.location.Location;

/**
 * Interface that should be implemented by any class that wished to receive location updates
 */
public interface LocationListener {

    /**
     * Method that is called by LocationUtils on any registered listener
     * @param location
     */
    public void locationUpdate(Location location);
}
