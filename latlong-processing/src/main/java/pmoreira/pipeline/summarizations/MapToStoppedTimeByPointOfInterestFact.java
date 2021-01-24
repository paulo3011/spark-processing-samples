package pmoreira.pipeline.summarizations;

import org.apache.parquet.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.spark.api.java.function.FlatMapFunction;
import pmoreira.domain.models.StoppedTimeByPointOfInterestFact;
import pmoreira.domain.business.StoppedTimeByPlate;
import pmoreira.domain.models.StoppedTimeByPoi;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MapToStoppedTimeByPointOfInterestFact
        implements FlatMapFunction<Iterator<StoppedTimeByPlate>, StoppedTimeByPointOfInterestFact>, Serializable
{
    @Override
    public Iterator<StoppedTimeByPointOfInterestFact> call(Iterator<StoppedTimeByPlate> stoppedTimeByPlateIterator) {
        final List<StoppedTimeByPointOfInterestFact> result = new ObjectArrayList<>();

        while (stoppedTimeByPlateIterator.hasNext()) {
            final StoppedTimeByPlate summarization = stoppedTimeByPlateIterator.next();
            List<StoppedTimeByPointOfInterestFact> converted = Convert(summarization);
            result.addAll(converted);
        }

        return result.iterator();
    }

    private List<StoppedTimeByPointOfInterestFact> Convert(StoppedTimeByPlate summarization) {
        List<StoppedTimeByPointOfInterestFact> result = new ObjectArrayList<>();

        final HashMap<String, StoppedTimeByPoi>  poiSummarization = summarization.getStoppedTimeByPoi();

        for(final String key : summarization.getStoppedTimeByPoi().keySet())
        {
            try {
                final StoppedTimeByPoi stoppedTimeByPoi = poiSummarization.get(key);
                StoppedTimeByPointOfInterestFact stoppedTimeByPoiFact = new StoppedTimeByPointOfInterestFact();
                final double totalSecondsStoppedInsidePoi = stoppedTimeByPoi.getTotalSecondsStoppedInsidePoi().getSum();
                final double totalSecondsInsidePoi = stoppedTimeByPoi.getTotalSecondsInsidePoi().getSum();
                stoppedTimeByPoiFact.setTotalSecondsStoppedInsidePoi(totalSecondsStoppedInsidePoi);
                stoppedTimeByPoiFact.setTotalSecondsInsidePoi(totalSecondsInsidePoi);
                stoppedTimeByPoiFact.setPointOfInterest(stoppedTimeByPoi.getPointOfInterest().getName());
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
