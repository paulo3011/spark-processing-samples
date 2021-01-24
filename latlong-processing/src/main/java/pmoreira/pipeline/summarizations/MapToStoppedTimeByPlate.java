package pmoreira.pipeline.summarizations;

import org.apache.parquet.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.spark.api.java.function.FlatMapFunction;
import pmoreira.domain.models.Position;
import pmoreira.domain.business.StoppedTimeByPlate;
import scala.Tuple2;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class MapToStoppedTimeByPlate
        implements FlatMapFunction<Iterator<Tuple2<String, List<Position>>>, StoppedTimeByPlate>, Serializable
{
    @Override
    public Iterator<StoppedTimeByPlate> call(Iterator<Tuple2<String, List<Position>>> positionByPlate)
            throws Exception
    {
        final List<StoppedTimeByPlate> summarizations = new ObjectArrayList<StoppedTimeByPlate>();

        while (positionByPlate.hasNext())
        {
            Tuple2<String,List<Position>> positions = positionByPlate.next();
            StoppedTimeByPlate summarization = new StoppedTimeByPlate();
            summarization.setPlate(positions._1);
            summarization.ProcessAllPosition(positions._2());
            summarizations.add(summarization);
        }

        return summarizations.iterator();
    }
}
