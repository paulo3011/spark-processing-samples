package pmoreira.pipeline.summarizations;

import org.apache.parquet.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.spark.api.java.function.FlatMapFunction;
import pmoreira.domain.models.StoppedTimeByPoiFact;
import pmoreira.domain.models.TimeByPoiAndPlateFact;
import scala.Tuple2;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class MapToStoppedTimeByPoiFact
        implements FlatMapFunction<Iterator<Tuple2<String, Iterable<TimeByPoiAndPlateFact>>>, StoppedTimeByPoiFact>, Serializable
{
    @Override
    public Iterator<StoppedTimeByPoiFact> call(Iterator<Tuple2<String, Iterable<TimeByPoiAndPlateFact>>> tuple2Iterator) {
        final List<StoppedTimeByPoiFact> result = new ObjectArrayList<>();

        while (tuple2Iterator.hasNext())
        {
            Tuple2<String, Iterable<TimeByPoiAndPlateFact>> timeByPoiAndPlateKeyPair = tuple2Iterator.next();
            StoppedTimeByPoiFact summarization = new StoppedTimeByPoiFact();
            summarization.setPointOfInterest(timeByPoiAndPlateKeyPair._1);
            final long total = this.sumTotalStoppedTime(timeByPoiAndPlateKeyPair._2);
            summarization.setTotalSecondsStopped(total);

            result.add(summarization);
        }

        return result.iterator();
    }

    private long sumTotalStoppedTime(Iterable<TimeByPoiAndPlateFact> timeByPoiAndPlateFacts) {
        long total=0;
        for(final TimeByPoiAndPlateFact time : timeByPoiAndPlateFacts){
            total += time.getTotalSecondsStoppedInsidePoi();
        }
        return total;
    }
}
