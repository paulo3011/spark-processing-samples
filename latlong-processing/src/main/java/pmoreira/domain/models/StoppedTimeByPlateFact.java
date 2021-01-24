package pmoreira.domain.models;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Total of seconds stopped by plate
 */
public class StoppedTimeByPlateFact implements Serializable {
    /**
     * Plate
     */
    @Getter @Setter
    private String plate = "";

    /**
     * Total of seconds stopped
     */
    @Getter @Setter
    private double totalSecondsStopped = 0;

    @Override
    public String toString() {
        return plate + "," + totalSecondsStopped;
    }
}
