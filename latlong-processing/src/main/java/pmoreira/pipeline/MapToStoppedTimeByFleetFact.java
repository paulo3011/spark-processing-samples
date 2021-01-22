package pmoreira.pipeline;

import org.apache.parquet.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.spark.api.java.function.FlatMapFunction;
import pmoreira.domain.models.StoppedTimeByFleetFact;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class MapToStoppedTimeByFleetFact
        implements FlatMapFunction<Iterator<StoppedTimeByPlate>, StoppedTimeByFleetFact>, Serializable
{
    @Override
    public Iterator<StoppedTimeByFleetFact> call(Iterator<StoppedTimeByPlate> stoppedTimeByPlateIterator)
            throws Exception {
        final List<StoppedTimeByFleetFact> result = new ObjectArrayList<StoppedTimeByFleetFact>();

        boolean isHeader = true;

        while (stoppedTimeByPlateIterator.hasNext()) {
            final StoppedTimeByPlate summarization = stoppedTimeByPlateIterator.next();
            List<StoppedTimeByFleetFact> converted = Convert(summarization);
            result.addAll(converted);
        }

        return result.iterator();
    }

    private List<StoppedTimeByFleetFact> Convert(StoppedTimeByPlate summarization) {
        List<StoppedTimeByFleetFact> stoppedTimeByFleetFactList = new ObjectArrayList<StoppedTimeByFleetFact>();

        for(final StoppedTimeByPoi stoppedTimeByPoi : summarization.getStoppedTimeByPoi())
        {
            StoppedTimeByFleetFact stoppedTimeByFleetFact = new StoppedTimeByFleetFact();
            final double totalSecondsStoppedInsidePoi = stoppedTimeByPoi.getTotalSecondsStoppedInsidePoi().getSum();
            final double totalSecondsInsidePoi = stoppedTimeByPoi.getTotalSecondsInsidePoi().getSum();
            stoppedTimeByFleetFact.setTotalSecondsStoppedInsidePoi(totalSecondsStoppedInsidePoi);
            stoppedTimeByFleetFact.setTotalSecondsInsidePoi(totalSecondsInsidePoi);
            stoppedTimeByFleetFact.setPointOfInterest(stoppedTimeByPoi.getPointOfInterest().getName());
        }
        //stoppedTimeByFleetFact.setTotalSecondsInsidePoi(summarization.getT);

        return stoppedTimeByFleetFactList;
    }
}
