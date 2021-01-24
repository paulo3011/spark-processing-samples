package pmoreira.pipeline.summarizations;

import org.apache.parquet.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.spark.api.java.function.FlatMapFunction;
import pmoreira.domain.models.Position;
import pmoreira.domain.business.TimeByPlate;
import scala.Tuple2;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class MapToStoppedTimeByPlate
        implements FlatMapFunction<Iterator<Tuple2<String, List<Position>>>, TimeByPlate>, Serializable
{
    @Override
    public Iterator<TimeByPlate> call(Iterator<Tuple2<String, List<Position>>> positionByPlate)
            throws Exception
    {
        final List<TimeByPlate> summarizations = new ObjectArrayList<TimeByPlate>();

        while (positionByPlate.hasNext())
        {
            Tuple2<String,List<Position>> positions = positionByPlate.next();
            TimeByPlate summarization = new TimeByPlate();
            summarization.setPlate(positions._1);
            summarization.ProcessAllPosition(positions._2());
            summarizations.add(summarization);
        }

        return summarizations.iterator();
    }
}
