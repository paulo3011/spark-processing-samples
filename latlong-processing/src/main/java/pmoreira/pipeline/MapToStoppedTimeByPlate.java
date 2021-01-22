package pmoreira.pipeline;

import org.apache.parquet.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.spark.api.java.function.FlatMapFunction;
import pmoreira.domain.models.Position;
import scala.Tuple2;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class MapToStoppedTimeByPlate
        implements FlatMapFunction<Iterator<Tuple2<String, List<Position>>>, StoppedTimeByPlate>, Serializable
{
    @Override
    public Iterator<StoppedTimeByPlate> call(Iterator<Tuple2<String, List<Position>>> positionByPlateAndParitions)
            throws Exception
    {
        final List<StoppedTimeByPlate> summarizations = new ObjectArrayList<StoppedTimeByPlate>();

        while (positionByPlateAndParitions.hasNext())
        {
            Tuple2<String,List<Position>> positions = positionByPlateAndParitions.next();
            StoppedTimeByPlate summarization = new StoppedTimeByPlate();
            summarization.setPlate(positions._1);
            summarization.ProcessAllPosition(positions._2());
            summarizations.add(summarization);
        }

        return summarizations.iterator();
    }
}
