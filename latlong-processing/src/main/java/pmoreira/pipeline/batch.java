package pmoreira.pipeline;

import org.apache.commons.io.FileUtils;
import org.apache.parquet.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import pmoreira.domain.models.Position;
import pmoreira.domain.models.StoppedTimeByPlateFact;
import scala.reflect.io.Directory;
import scala.reflect.io.File;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

public class batch {
    public static void main(String[] args) throws ParseException, IOException {
        System.out.println("Starting batch processing");
        //System.setProperty("hadoop.home.dir", "C:\\Users\\moreira\\bin\\hadoop-3.3.0\\");

        /**
         * The first thing a Spark program must do is to create a JavaSparkContext object, which tells Spark how to access a cluster.
         * To create a SparkContext you first need to build a SparkConf object that contains information about your application.
         */
        SparkConf sparkConf = new SparkConf()
                .setAppName("PositionProcessing")
                .setMaster("local[*]");  // Delete this line when submitting to a cluster

        SparkSession sparkSession = SparkSession
                .builder()
                .config(sparkConf)
                .getOrCreate();

        //SparkContext sparkContext = sparkSession.sparkContext();
        JavaSparkContext sparkContext = JavaSparkContext.fromSparkContext(sparkSession.sparkContext()) ;//new JavaSparkContext(sparkConf);

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

        //stoppedTimeByPlateFactRDD.collect().forEach(summarization -> { String str = summarization.toString(); System.out.println(str);  });

        final Dataset<Row> rowDataset = sparkSession.createDataFrame(stoppedTimeByPlateFactRDD,StoppedTimeByPlateFact.class);

        /*
        for (Row row : rowDataset.collectAsList()) {
            try {
                System.out.println(String.format("Plate:%s, ", row.get(0).toString()));
            }catch (Exception ex){
                System.out.println(ex);
            }
        }*/

        String outputPath = "C:\\tmp\\positions\\csv";
        Path path = Paths.get(outputPath);

        FileUtils.deleteDirectory(path.toFile());

        rowDataset.repartition(1)
                .write()
                //.bucketBy(1,"plate")
                .option("header","true")
                .csv(outputPath);
    }


}
