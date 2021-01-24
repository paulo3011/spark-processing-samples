package pmoreira.domain.models;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Total of seconds stopped by point of interest (POI)
 */
public class StoppedTimeByPoiFact implements Serializable {
    /**
     * Point of interest name
     */
    @Getter @Setter
    private String pointOfInterest = "";

    /**
     * Total of seconds stopped
     */
    @Getter @Setter
    private double totalSecondsStopped = 0;

    @Override
    public String toString() {
        return pointOfInterest + "," + totalSecondsStopped;
    }
}
