package pmoreira.pipeline;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.parquet.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import pmoreira.domain.contracts.IPositionProcessing;
import pmoreira.domain.models.PointOfInterest;
import pmoreira.domain.models.Position;
import pmoreira.domain.models.PositionProcessingBase;

import java.util.HashMap;
import java.util.List;

public class StoppedTimeByPlate extends PositionProcessingBase implements IPositionProcessing {
    /**
     * Vehicle plate
     */
    @Getter @Setter
    private String plate;
    /**
     * Summarization property that holds the total of seconds stopped by vehicle
     * - Tempo total parado por veículo, independente do POI
     */
    @Getter @Setter
    private SummaryStatistics totalStoppedTime = new SummaryStatistics();

    /**
     * Summarizations values by point of interest
     * - Quantidade de tempo que os veículos passaram parados dentro de cada POI
     * - Quantidade de tempo que os veículos estavam dentro de cada POI
     */
    @Getter @Setter
    private List<StoppedTimeByPoi> stoppedTimeByPoi = new ObjectArrayList<StoppedTimeByPoi>();
    /**
     * Summarizations values by point of interest
     * - Quantidade de tempo que os veículos passaram parados dentro de cada POI
     * - Quantidade de tempo que os veículos estavam dentro de cada POI
     */
    @Getter @Setter
    private HashMap<String,StoppedTimeByPoi> stoppedTimeByPois = new HashMap<>();

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

    public void ProcessNextPosition(final Position currentPosition)
    {
        if(this.previousPosition == null)
            return;

        if(currentPosition.isStopped() == false)
            return;

        final long timeStoppedToAdd = this.previousPosition.getSecondsFromNext(currentPosition);
        totalStoppedTime.addValue(timeStoppedToAdd);

        this.processPointOfInterest(currentPosition);
    }

    /**
     * Process point of interest
     * @param currentPosition
     */
    public void processPointOfInterest(Position currentPosition){
        if(this.previousNearestPointOfInterest == null)
            return;

        final PointOfInterest currentPointOfInterest = currentPosition.getNearestPointOfInterest();
        StoppedTimeByPoi stoppedTimeByPoi = new StoppedTimeByPoi();

        if(stoppedTimeByPois.containsKey(currentPointOfInterest.getName()))
            stoppedTimeByPoi = stoppedTimeByPois.get(currentPointOfInterest.getName());

        long timeStoppedInsidePoi = 0;
        long timeInsidePoi = 0;
        boolean isSamePointOfInterest = (currentPointOfInterest != null
                && this.previousNearestPointOfInterest != null
                && currentPointOfInterest.getName().equals(this.previousNearestPointOfInterest.getName()));

        if(isSamePointOfInterest)
            timeInsidePoi = this.previousPosition.getSecondsFromNext(currentPosition);

        if(currentPosition.isStopped()){
            if(isSamePointOfInterest)
                timeStoppedInsidePoi = timeInsidePoi;
        }

        stoppedTimeByPoi.getTotalSecondsInsidePoi().addValue(timeInsidePoi);
        stoppedTimeByPoi.getTotalSecondsStoppedInsidePoi().addValue(timeStoppedInsidePoi);

        stoppedTimeByPois.put(currentPointOfInterest.getName(), stoppedTimeByPoi);
    }

    public void ProcessAllPosition(final List<Position> positionList)
    {
        for(final Position currentPosition : positionList)
        {
            this.ProcessNextPosition(currentPosition);
            this.previousPosition = currentPosition;
            this.previousNearestPointOfInterest = currentPosition.getNearestPointOfInterest();
        }
    }

}
