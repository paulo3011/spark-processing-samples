package pmoreira.pipeline;

import org.apache.parquet.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import scala.Serializable;
import scala.Tuple2;

import java.util.Iterator;
import java.util.List;


public class MapGroupPositionByPlate
        implements PairFlatMapFunction<Iterator<Position>, String, Position>, Serializable
{
    @Override
    public Iterator<Tuple2<String, Position>> call(Iterator<Position> positionIteratorPerPartition) throws Exception {

        final List<Tuple2<String,Position>> positions = new ObjectArrayList<Tuple2<String, Position>>();

        while(positionIteratorPerPartition.hasNext())
        {
            final Position position = positionIteratorPerPartition.next();
            final Tuple2<String, Position> platePositionPair = this.MapToPlatePositionPair(position);
            positions.add(platePositionPair);
        }

        return positions.iterator();
    }

    private Tuple2<String, Position> MapToPlatePositionPair(Position position) {
        return new Tuple2<>(position.getPlate(), position);
    }
}
