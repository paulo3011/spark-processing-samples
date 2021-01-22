package pmoreira.pipeline;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.parquet.it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.io.Serializable;
import java.util.List;

public class StoppedTimeByFleet implements Serializable {
    /**
     * Fleet
     */
    @Getter
    @Setter
    private String fleet = "all";

    /**
     * Summarizations values by point of interest
     * - Tempo total da frota gasto parado em cada POI
     */
    @Getter
    @Setter
    private List<StoppedTimeByPoi> stoppedTimeByPoi = new ObjectArrayList<StoppedTimeByPoi>();
}
