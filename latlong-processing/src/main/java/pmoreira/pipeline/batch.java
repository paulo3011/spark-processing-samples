package pmoreira.pipeline;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.SparkConf;
import pmoreira.domain.models.Position;
import pmoreira.domain.models.StoppedTimeByPlateFact;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

public class batch {
    public static void main(String[] args) throws ParseException {
        System.out.println("Starting batch processing");

        /**
         * The first thing a Spark program must do is to create a JavaSparkContext object, which tells Spark how to access a cluster.
         * To create a SparkContext you first need to build a SparkConf object that contains information about your application.
         */
        SparkConf sparkConf = new SparkConf()
                .setAppName("Example Spark App")
                .setMaster("local[*]");  // Delete this line when submitting to a cluster

        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);
        List<Integer> data = Arrays.asList(1, 2, 3, 4, 5);
        JavaRDD<Integer> distData = sparkContext.parallelize(data);

        String positions = "C:\\projetos\\paulo3011\\spark-processing-samples\\latlong-processing\\src\\Data\\posicoes.csv";
        JavaRDD<String> positionFileLines = sparkContext.textFile(positions);

        JavaRDD<Position> positionsRDD = positionFileLines.mapPartitions(new MapCsvToPosition());

        //positionsRDD.collect().forEach(position -> {System.out.println("plate:" + position.getPlate()); });

        JavaPairRDD<String,Position> positionByPlatePairRDD = positionsRDD.mapPartitionsToPair(new MapToPairPlatePosition());

        //positionByPlatePairRDD.collect().forEach(stringPositionTuple2 -> { System.out.println("plate: " + stringPositionTuple2._1);  });

        JavaPairRDD<String,List<Position>> positionListByPlateRDD = new MapToListPositionByPlate().combinePositionsByPlate(positionByPlatePairRDD);

        /*
        positionListByPlateRDD.collect().forEach(stringPositionTuple2 -> {
            System.out.println("plate: " + stringPositionTuple2._1 + " positions size: " +stringPositionTuple2._2.size());
        });
        */

        //Tempo total parado por veículo, independente do POI.
        JavaRDD<StoppedTimeByPlate> stoppedTimeByPlateJavaRDD = positionListByPlateRDD.mapPartitions(new MapToStoppedTimeByPlate());

        //stoppedTimeByPlateJavaRDD.collect().forEach(summarization -> { String str = summarization.toString();  System.out.println(str); });

        JavaRDD<StoppedTimeByPlateFact> stoppedTimeByPlateFactRDD = stoppedTimeByPlateJavaRDD.mapPartitions(new MapToStoppedTimeByPlateFact());

        stoppedTimeByPlateFactRDD.collect().forEach(summarization -> {
            String str = summarization.toString();
            System.out.println(str);
        });

        stoppedTimeByPlateFactRDD.saveAsTextFile("C:\\projetos\\paulo3011\\spark-processing-samples\\latlong-processing\\src\\Data\\output\\stoppedTimeByPlateFact");
    }


}
