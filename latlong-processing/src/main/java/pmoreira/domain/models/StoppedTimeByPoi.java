package pmoreira.domain.models;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import pmoreira.domain.models.PointOfInterest;

import java.io.Serializable;

/**
 * Represent the summarization values by point of interest:
 * - total of seconds stopped inside of poi
 * - total of seconds inside of poi
 */
public class StoppedTimeByPoi implements Serializable {
    /**
     * Point of interest who belongs the summarization.
     */
    @Getter
    @Setter
    private PointOfInterest pointOfInterest;
    /**
     * Summarization property that holds the total of seconds stopped inside of poi.
     */
    @Getter
    @Setter
    private SummaryStatistics totalSecondsStoppedInsidePoi = new SummaryStatistics();
    /**
     * Summarization property that holds the total of seconds inside of poi.
     */
    @Getter
    @Setter
    private SummaryStatistics totalSecondsInsidePoi = new SummaryStatistics();
}
