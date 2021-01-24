package pmoreira.domain.models;

import com.grum.geocalc.BoundingArea;
import com.grum.geocalc.Coordinate;
import com.grum.geocalc.EarthCalc;
import com.grum.geocalc.Point;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

public class Position
        implements Serializable
{
    /**
     * Set or get Vehicle plate
     */
    @Getter @Setter
    private String plate;
    /**
     * Set or get the date when it was received the position
     */
    @Getter @Setter
    private LocalDateTime positionDate;

    /**
     * Set or get the vehicle speed at the position
     */
    @Getter @Setter
    private int speed;

    @Getter @Setter
    private float latitude;

    @Getter @Setter
    private float longitude;

    /**
     * Set or get if the vehicle ignition is on or off.
     * True on and False off.
     */
    @Getter @Setter
    private boolean ignition;

    /**
     * Nearest point of interest
     */
    @Getter @Setter
    private PointOfInterest nearestPointOfInterest=null;

    public Position(){

    }


    public Position(String plate, LocalDateTime positionDate, double latitude, double longitude, int speed, boolean ignition){
        this.plate = plate;
        this.positionDate = positionDate;
        this.latitude = (float)latitude;
        this.longitude = (float)longitude;
        this.speed = speed;
        this.ignition = ignition;
    }

    public Position(String plate, LocalDateTime positionDate, double latitude, double longitude, int speed, boolean ignition, PointOfInterest pointOfInterest){
        this.plate = plate;
        this.positionDate = positionDate;
        this.latitude = (float)latitude;
        this.longitude = (float)longitude;
        this.speed = speed;
        this.ignition = ignition;
        this.nearestPointOfInterest = pointOfInterest;
    }


    @Override
    public String toString() {
        String poiDescription = (nearestPointOfInterest != null) ? nearestPointOfInterest.toString() : "-";
        return plate + ","
                + positionDate.toString() + ","
                + speed + ","
                + longitude + ","
                + latitude + ","
                + ignition + ","
                + isStopped() + ","
                + poiDescription
                ;
    }

    /**
     * Check if vehicle is stopped or not
     * @return Return true if stopped or false otherwise
     */
    public boolean isStopped()
    {
        return speed < 5 && !ignition;
    }

    /**
     * Return the difference between next position and this position.
     * @param nextPosition Next position
     * @return Return the amount of seconds from this position to nextPosition
     */
    public long getSecondsFromNext(final Position nextPosition)
    {
        Duration duration = Duration.between(this.positionDate, nextPosition.positionDate);
        return duration.getSeconds();
    }

    /**
     * Logic to order position list by plate and position date.
     * @param position1 Position 1
     * @param position2 Position 2
     * @return Return the order int
     */
    public static int orderByPositionDate(final Position position1, final Position position2)
    {
        int lastComparison;
        lastComparison = position1.getPlate().compareTo(position2.getPlate());

        if(lastComparison == 0){
            lastComparison = position1.getPositionDate().compareTo(position2.getPositionDate());
        }

        return lastComparison;
    }

    /**
     * Check if this position is inside of point of interest
     * @param pointOfInterest Poi
     * @return Return true if is inside and false otherwise
     */
    public boolean isInsidePointOfInterest(PointOfInterest pointOfInterest) {
        Coordinate lat = Coordinate.fromDegrees(this.getLatitude());
        Coordinate lng = Coordinate.fromDegrees(this.getLongitude());
        Point p1 = Point.at(lat, lng);

        lat = Coordinate.fromDegrees(pointOfInterest.getLatitude());
        lng = Coordinate.fromDegrees(pointOfInterest.getLongitude());
        Point poi = Point.at(lat, lng);

        BoundingArea boundingArea = EarthCalc.gcd.around(poi,pointOfInterest.getRadius());

        return boundingArea.contains(p1);
    }
}
