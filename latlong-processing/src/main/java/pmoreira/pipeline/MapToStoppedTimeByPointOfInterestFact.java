package pmoreira.pipeline;

import org.apache.parquet.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.spark.api.java.function.FlatMapFunction;
import pmoreira.domain.models.StoppedTimeByPointOfInterestFact;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class MapToStoppedTimeByPointOfInterestFact
        implements FlatMapFunction<Iterator<StoppedTimeByPlate>, StoppedTimeByPointOfInterestFact>, Serializable
{
    @Override
    public Iterator<StoppedTimeByPointOfInterestFact> call(Iterator<StoppedTimeByPlate> stoppedTimeByPlateIterator)
            throws Exception {
        final List<StoppedTimeByPointOfInterestFact> result = new ObjectArrayList<StoppedTimeByPointOfInterestFact>();

        boolean isHeader = true;

        while (stoppedTimeByPlateIterator.hasNext()) {
            final StoppedTimeByPlate summarization = stoppedTimeByPlateIterator.next();
            List<StoppedTimeByPointOfInterestFact> converted = Convert(summarization);
            result.addAll(converted);
        }

        return result.iterator();
    }

    private List<StoppedTimeByPointOfInterestFact> Convert(StoppedTimeByPlate summarization) {
        List<StoppedTimeByPointOfInterestFact> stoppedTimeByPointOfInterestFactList = new ObjectArrayList<StoppedTimeByPointOfInterestFact>();

        for(final StoppedTimeByPoi stoppedTimeByPoi : summarization.getStoppedTimeByPoi())
        {
            StoppedTimeByPointOfInterestFact stoppedTimeByPointOfInterestFact = new StoppedTimeByPointOfInterestFact();
            final double totalSecondsStoppedInsidePoi = stoppedTimeByPoi.getTotalSecondsStoppedInsidePoi().getSum();
            final double totalSecondsInsidePoi = stoppedTimeByPoi.getTotalSecondsInsidePoi().getSum();
            stoppedTimeByPointOfInterestFact.setTotalSecondsStoppedInsidePoi(totalSecondsStoppedInsidePoi);
            stoppedTimeByPointOfInterestFact.setTotalSecondsInsidePoi(totalSecondsInsidePoi);
            stoppedTimeByPointOfInterestFact.setPointOfInterest(stoppedTimeByPoi.getPointOfInterest().getName());
        }
        //stoppedTimeByFleetFact.setTotalSecondsInsidePoi(summarization.getT);

        return stoppedTimeByPointOfInterestFactList;
    }
}
