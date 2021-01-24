package pmoreira.pipeline.rddmaps;

import org.apache.parquet.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.spark.api.java.function.FlatMapFunction;
import pmoreira.domain.models.StoppedTimeByPlateFact;
import pmoreira.domain.business.TimeByPlateProcessor;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class MapToStoppedTimeByPlateFact
        implements FlatMapFunction<Iterator<TimeByPlateProcessor>, StoppedTimeByPlateFact>, Serializable
{
    @Override
    public Iterator<StoppedTimeByPlateFact> call(Iterator<TimeByPlateProcessor> stoppedTimeByPlateIterator) {
        final List<StoppedTimeByPlateFact> result = new ObjectArrayList<>();

        while (stoppedTimeByPlateIterator.hasNext()) {
            final TimeByPlateProcessor summarization = stoppedTimeByPlateIterator.next();
            StoppedTimeByPlateFact converted = convert(summarization);
            result.add(converted);
        }

        return result.iterator();
    }

    private StoppedTimeByPlateFact convert(TimeByPlateProcessor timeByPlateProcessor) {
        StoppedTimeByPlateFact fact = new StoppedTimeByPlateFact();
        fact.setPlate(timeByPlateProcessor.getPlate());
        final double totalStoppedTime = timeByPlateProcessor.getTotalStoppedTime().getSum();
        fact.setTotalSecondsStopped(totalStoppedTime);
        return fact;
    }
}
