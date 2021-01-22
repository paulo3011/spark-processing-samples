package pmoreira.pipeline;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.io.Serializable;
import java.time.LocalDateTime;

public class StoppedTime implements Serializable {
    /**
     * Vehicle plate
     */
    @Getter
    @Setter
    private String plate;
    /**
     * Summarization property that holds the total of seconds stopped by vehicle
     */
    @Getter
    @Setter
    private SummaryStatistics totalStoppedTime = new SummaryStatistics();
}
