package pmoreira.pipeline;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.parquet.it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class StoppedTimeByPlate implements Serializable {
    /**
     * Vehicle plate
     */
    @Getter
    @Setter
    private String plate;
    /**
     * Summarization property that holds the total of seconds stopped by vehicle
     * - Tempo total parado por veículo, independente do POI
     */
    @Getter
    @Setter
    private SummaryStatistics totalStoppedTime = new SummaryStatistics();

    /**
     * Summarizations values by point of interest
     * - Quantidade de tempo que os veículos passaram parados dentro de cada POI
     * - Quantidade de tempo que os veículos estavam dentro de cada POI
     */
    @Getter
    @Setter
    private List<StoppedTimeByPoi> stoppedTimeByPoi = new ObjectArrayList<StoppedTimeByPoi>();
}
