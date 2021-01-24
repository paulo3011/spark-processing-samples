package pmoreira.pipeline.summarizations;

import org.apache.parquet.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.spark.api.java.function.FlatMapFunction;
import pmoreira.domain.models.Position;
import pmoreira.domain.business.TimeByPlateProcessor;
import scala.Tuple2;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class MapToStoppedTimeByPlate
        implements FlatMapFunction<Iterator<Tuple2<String, Iterable<Position>>>, TimeByPlateProcessor>, Serializable
{
    @Override
    public Iterator<TimeByPlateProcessor> call(Iterator<Tuple2<String, Iterable<Position>>> tuple2Iterator) {
        final List<TimeByPlateProcessor> summarizations = new ObjectArrayList<>();

        while (tuple2Iterator.hasNext())
        {
            Tuple2<String, Iterable<Position>> positionsKeyPair = tuple2Iterator.next();
            TimeByPlateProcessor summarization = new TimeByPlateProcessor();
            summarization.setPlate(positionsKeyPair._1);
            summarization.ProcessAllPosition(positionsKeyPair._2());
            summarizations.add(summarization);
        }

        return summarizations.iterator();
    }
}
