package pmoreira.pipeline.rddmaps;

import org.apache.parquet.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.spark.api.java.JavaPairRDD;
import pmoreira.domain.models.Position;

import java.io.Serializable;
import java.util.List;

public class MapToListPositionByPlate implements Serializable {
    /**
     * Create a new Pair RDD grouping positions by plate and sorting inside each group by position date.
     * @param positionJavaPairRDD
     * @return
     */
    public JavaPairRDD<String,List<Position>> groupPositionsByPlate(JavaPairRDD<String,Position> positionJavaPairRDD){
        return positionJavaPairRDD.combineByKey(
                this::createNewPositionListForEachPlate,
                this::mergePositionsFromSamePlate,
                this::mergeListPositionFromSamePlate
        );
    }

    /**
     * Create a new position list for each new plate found.
     * @param firstPosition
     * @return
     */
    private List<Position> createNewPositionListForEachPlate(final Position firstPosition)
    {
        List<Position> positions = new ObjectArrayList<Position>();
        positions.add(firstPosition);
        return positions;
    }

    /**
     * Add the position to one list of the same plate.
     * @param positionsByPlate
     * @param positionSamePlate
     * @return
     */
    private List<Position> mergePositionsFromSamePlate(final List<Position> positionsByPlate, final Position positionSamePlate)
    {
        positionsByPlate.add(positionSamePlate);
        return positionsByPlate;
    }

    /**
     * Join together position lists of the same plate
     * @param positionsByPlate1
     * @param positionsByPlate2
     * @return
     */
    private List<Position> mergeListPositionFromSamePlate(final List<Position> positionsByPlate1, final List<Position> positionsByPlate2)
    {
        if(positionsByPlate2 != null)
            positionsByPlate1.addAll(positionsByPlate1);

        positionsByPlate1.sort(Position::orderByPositionDate);

        return positionsByPlate1;
    }
}
