package pmoreira.pipeline;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.parquet.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import pmoreira.domain.contracts.IPositionProcessing;
import pmoreira.domain.models.Position;
import pmoreira.domain.models.PositionProcessingBase;

import java.util.List;

public class StoppedTimeByPlate extends PositionProcessingBase implements IPositionProcessing {
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

    @Getter
    public Position previousPosition = null;

    @Override
    public String toString() {
        return "StoppedTimeByPlate{" +
                "plate='" + plate + '\'' +
                ", totalStoppedTime=" + totalStoppedTime.getSum() +
                ", stoppedTimeByPoi=" + stoppedTimeByPoi +
                ", previousPosition=" + previousPosition +
                '}';
    }

    public void ProcessNextPosition(final Position nextPosition)
    {
        if(this.previousPosition == null)
            return;

        if(nextPosition.isStopped() == false)
            return;

        final long timeStoppedToAdd = previousPosition.getSecondsFromNext(nextPosition);
        totalStoppedTime.addValue(timeStoppedToAdd);
    }

    public void ProcessAllPosition(final List<Position> positionList)
    {
        for(final Position nextPosition : positionList)
        {
            this.ProcessNextPosition(nextPosition);
            this.previousPosition = nextPosition;
        }
    }

}
