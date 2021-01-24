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
import java.util.List;

public class TimeByPlate extends PositionProcessingBase implements IPositionProcessing {
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

    public void ProcessNextPosition(final Position currentPosition)
    {
        if(this.previousPosition == null)
            return;
        this.ProcessStoppedTimeInsidePoi(currentPosition);
        this.processPointOfInterest(currentPosition);
    }

     public void ProcessStoppedTimeInsidePoi(Position currentPosition){
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
