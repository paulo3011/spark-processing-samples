package pmoreira.domain.models;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Tempo total parado por veículo, independente do POI.
 */
public class StoppedTimeByPlateFact implements Serializable {
    /**
     * Plate
     */
    @Getter
    @Setter
    private String plate = "";

    /**
     * Total of seconds stopped
     */
    @Getter
    @Setter
    private double totalSecondsStopped = 0;

    @Override
    public String toString() {
        return plate + "," + totalSecondsStopped;
    }
}
