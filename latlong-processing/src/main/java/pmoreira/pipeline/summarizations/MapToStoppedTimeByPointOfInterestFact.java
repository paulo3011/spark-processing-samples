package pmoreira.pipeline.summarizations;

import org.apache.parquet.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.spark.api.java.function.FlatMapFunction;
import pmoreira.domain.models.StoppedTimeByPointOfInterestFact;
import pmoreira.domain.business.TimeByPlate;
import pmoreira.domain.models.TimeByPoi;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MapToStoppedTimeByPointOfInterestFact
        implements FlatMapFunction<Iterator<TimeByPlate>, StoppedTimeByPointOfInterestFact>, Serializable
{
    @Override
    public Iterator<StoppedTimeByPointOfInterestFact> call(Iterator<TimeByPlate> stoppedTimeByPlateIterator) {
        final List<StoppedTimeByPointOfInterestFact> result = new ObjectArrayList<>();

        while (stoppedTimeByPlateIterator.hasNext()) {
            final TimeByPlate summarization = stoppedTimeByPlateIterator.next();
            List<StoppedTimeByPointOfInterestFact> converted = Convert(summarization);
            result.addAll(converted);
        }

        return result.iterator();
    }

    private List<StoppedTimeByPointOfInterestFact> Convert(TimeByPlate summarization) {
        List<StoppedTimeByPointOfInterestFact> result = new ObjectArrayList<>();

        final HashMap<String, TimeByPoi>  poiSummarization = summarization.getTimeByPoi();

        for(final String key : summarization.getTimeByPoi().keySet())
        {
            try {
                final TimeByPoi timeByPoi = poiSummarization.get(key);
                StoppedTimeByPointOfInterestFact stoppedTimeByPoiFact = new StoppedTimeByPointOfInterestFact();
                final double totalSecondsStoppedInsidePoi = timeByPoi.getTotalSecondsStoppedInsidePoi().getSum();
                final double totalSecondsInsidePoi = timeByPoi.getTotalSecondsInsidePoi().getSum();
                stoppedTimeByPoiFact.setTotalSecondsStoppedInsidePoi(totalSecondsStoppedInsidePoi);
                stoppedTimeByPoiFact.setTotalSecondsInsidePoi(totalSecondsInsidePoi);
                stoppedTimeByPoiFact.setPointOfInterest(timeByPoi.getPointOfInterest().getName());
                stoppedTimeByPoiFact.setPlate(summarization.getPlate());
                result.add(stoppedTimeByPoiFact);
            }
            catch (Exception ex){
                System.out.println(ex);
            }
        }
        //stoppedTimeByFleetFact.setTotalSecondsInsidePoi(summarization.getT);

        return result;
    }
}
