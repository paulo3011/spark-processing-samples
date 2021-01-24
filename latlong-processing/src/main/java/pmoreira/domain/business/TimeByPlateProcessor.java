package pmoreira.domain.business;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import pmoreira.domain.contracts.IPositionProcessing;
import pmoreira.domain.models.PointOfInterest;
import pmoreira.domain.models.Position;
import pmoreira.domain.models.PositionProcessingBase;
import pmoreira.domain.models.TimeByPoi;

import java.util.HashMap;

public class TimeByPlateProcessor extends PositionProcessingBase implements IPositionProcessing {
    /**
     * Vehicle plate
     */
    @Getter @Setter
    private String plate;
    /**
     * Summarization property that holds the total of seconds stopped by vehicle
     */
    @Getter @Setter
    private SummaryStatistics totalStoppedTime = new SummaryStatistics();

    /**
     * Summarizations values by point of interest
     */
    @Getter @Setter
    private HashMap<String, TimeByPoi> timeByPoi = new HashMap<>();

    @Getter
    public Position previousPosition = null;

    @Override
    public String toString() {
        return plate + ',' + totalStoppedTime.getSum();
    }

    public void processNextPosition(final Position currentPosition)
    {
        if(this.previousPosition == null)
            return;
        this.processStoppedTimeInsidePoi(currentPosition);
        this.processPointOfInterest(currentPosition);
    }

     public void processStoppedTimeInsidePoi(Position currentPosition){
        if(!currentPosition.isStopped())
            return;

        final long timeStoppedToAdd = this.previousPosition.getSecondsFromNext(currentPosition);
        totalStoppedTime.addValue(timeStoppedToAdd);
    }

    /**
     * Process point of interest
     * @param currentPosition current position to process
     */
    public void processPointOfInterest(Position currentPosition){
        if(this.previousNearestPointOfInterest == null || currentPosition.getNearestPointOfInterest() == null)
            return;

        final PointOfInterest currentPoi = currentPosition.getNearestPointOfInterest();
        TimeByPoi currentSummarization = new TimeByPoi();
        currentSummarization.setPointOfInterest(currentPoi);

        if(this.timeByPoi.containsKey(currentPoi.getName()))
            currentSummarization = this.timeByPoi.get(currentPoi.getName());

        long timeStoppedInsidePoi = 0;
        long timeInsidePoi = 0;
        boolean isSamePointOfInterest = (currentPoi.getName().equals(this.previousNearestPointOfInterest.getName()));

        if(isSamePointOfInterest)
            timeInsidePoi = this.previousPosition.getSecondsFromNext(currentPosition);

        if(currentPosition.isStopped()){
            if(isSamePointOfInterest)
                timeStoppedInsidePoi = timeInsidePoi;
        }

        currentSummarization.getTotalSecondsInsidePoi().addValue(timeInsidePoi);
        currentSummarization.getTotalSecondsStoppedInsidePoi().addValue(timeStoppedInsidePoi);

        this.timeByPoi.put(currentPoi.getName(), currentSummarization);
    }

    public void processAllPosition(final Iterable<Position> positionList)
    {
        for(final Position currentPosition : positionList)
        {
            this.processNextPosition(currentPosition);
            this.previousPosition = currentPosition;
            this.previousNearestPointOfInterest = currentPosition.getNearestPointOfInterest();
        }
    }
}
