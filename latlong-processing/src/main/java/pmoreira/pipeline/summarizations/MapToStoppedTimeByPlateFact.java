package pmoreira.pipeline.summarizations;

import org.apache.parquet.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.spark.api.java.function.FlatMapFunction;
import pmoreira.domain.models.StoppedTimeByPlateFact;
import pmoreira.domain.business.TimeByPlate;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class MapToStoppedTimeByPlateFact
        implements FlatMapFunction<Iterator<TimeByPlate>, StoppedTimeByPlateFact>, Serializable
{
    @Override
    public Iterator<StoppedTimeByPlateFact> call(Iterator<TimeByPlate> stoppedTimeByPlateIterator)
            throws Exception {
        final List<StoppedTimeByPlateFact> result = new ObjectArrayList<>();

        while (stoppedTimeByPlateIterator.hasNext()) {
            final TimeByPlate summarization = stoppedTimeByPlateIterator.next();
            StoppedTimeByPlateFact converted = Convert(summarization);
            result.add(converted);
        }

        return result.iterator();
    }

    private StoppedTimeByPlateFact Convert(TimeByPlate summarization) {
        StoppedTimeByPlateFact stoppedTimeByFleetFact = new StoppedTimeByPlateFact();
        stoppedTimeByFleetFact.setPlate(summarization.getPlate());
        final double totalStoppedTime = summarization.getTotalStoppedTime().getSum();
        stoppedTimeByFleetFact.setTotalSecondsStopped(totalStoppedTime);
        return stoppedTimeByFleetFact;
    }
}
