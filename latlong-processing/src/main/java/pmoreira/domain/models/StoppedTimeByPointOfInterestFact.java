package pmoreira.domain.models;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Total of seconds stopped by plate and point of interest.
 */
public class StoppedTimeByPointOfInterestFact implements Serializable {
    /**
     * Plate who belongs the stopped time.
     */
    @Getter @Setter
    private String plate = "";
    /**
     * Point of interest who belongs the summarization.
     */
    @Getter @Setter
    private String pointOfInterest;

    /**
     * Total of seconds stopped inside of poi.
     */
    @Getter @Setter
    private double totalSecondsStoppedInsidePoi = 0;

    /**
     * Total of seconds inside of poi. (plus)
     */
    @Getter @Setter
    private double totalSecondsInsidePoi = 0;
}
