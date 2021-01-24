package pmoreira.application.batchprocessing.maps.rdd;

import org.apache.parquet.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.spark.api.java.function.FlatMapFunction;
import pmoreira.domain.models.TimeByPoiAndPlateFact;
import pmoreira.domain.business.TimeByPlateProcessor;
import pmoreira.domain.models.TimeByPoi;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MapToTimeByPoiAndPlateFact
        implements FlatMapFunction<Iterator<TimeByPlateProcessor>, TimeByPoiAndPlateFact>, Serializable
{
    @Override
    public Iterator<TimeByPoiAndPlateFact> call(Iterator<TimeByPlateProcessor> stoppedTimeByPlateIterator) {
        final List<TimeByPoiAndPlateFact> result = new ObjectArrayList<>();

        while (stoppedTimeByPlateIterator.hasNext()) {
            final TimeByPlateProcessor summarization = stoppedTimeByPlateIterator.next();
            List<TimeByPoiAndPlateFact> converted = Convert(summarization);
            result.addAll(converted);
        }

        return result.iterator();
    }

    private List<TimeByPoiAndPlateFact> Convert(TimeByPlateProcessor summarization) {
        List<TimeByPoiAndPlateFact> result = new ObjectArrayList<>();

        final HashMap<String, TimeByPoi>  poiSummarization = summarization.getTimeByPoi();

        for(final String key : summarization.getTimeByPoi().keySet())
        {
            final TimeByPoi timeByPoi = poiSummarization.get(key);
            TimeByPoiAndPlateFact stoppedTimeByPoiAndPlateFact = new TimeByPoiAndPlateFact();
            final double totalSecondsStoppedInsidePoi = timeByPoi.getTotalSecondsStoppedInsidePoi().getSum();
            final double totalSecondsInsidePoi = timeByPoi.getTotalSecondsInsidePoi().getSum();
            stoppedTimeByPoiAndPlateFact.setTotalSecondsStoppedInsidePoi(totalSecondsStoppedInsidePoi);
            stoppedTimeByPoiAndPlateFact.setTotalSecondsInsidePoi(totalSecondsInsidePoi);
            stoppedTimeByPoiAndPlateFact.setPointOfInterest(timeByPoi.getPointOfInterest().getName());
            stoppedTimeByPoiAndPlateFact.setPlate(summarization.getPlate());
            result.add(stoppedTimeByPoiAndPlateFact);
        }

        return result;
    }
}
