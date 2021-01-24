package pmoreira.domain.models;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Represent one point of interest
 */
public class PointOfInterest implements Serializable {
    /**
     * Point of interest name
     */
    @Getter @Setter
    private String name = "";

    /**
     * The center latitude of point of interest
     */
    @Getter @Setter
    private float latitude;

    /**
     * The center longitude of point of interest
     */
    @Getter @Setter
    private float longitude;

    /**
     * The radius in meter of point of reference
     */
    @Getter @Setter
    private float radius;

    public PointOfInterest(){

    }

    /**
     *
     * @param name Name of poi
     * @param latitude The center latitude of point of interest
     * @param longitude The center longitude of point of interest
     * @param radius The radius in meter of point of reference
     */
    public PointOfInterest(String name, double latitude, double longitude, float radius){
        this.name = name;
        this.latitude = (float)latitude;
        this.longitude = (float)longitude;
        this.radius = radius;
    }

    @Override
    public String toString() {
        return name + ","
                + longitude + ","
                + latitude + ","
                + radius
                ;
    }
}
