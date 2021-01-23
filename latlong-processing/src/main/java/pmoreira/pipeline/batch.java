package pmoreira.pipeline;

import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apache.parquet.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.SparkConf;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.storage.StorageLevel;
import org.jetbrains.annotations.NotNull;
import pmoreira.domain.models.PointOfInterest;
import pmoreira.domain.models.Position;
import pmoreira.domain.models.StoppedTimeByPlateFact;
import pmoreira.domain.models.StoppedTimeByPointOfInterestFact;
import scala.reflect.io.Directory;
import scala.reflect.io.File;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        //GelPointOfInterest();

        //SparkContext sparkContext = sparkSession.sparkContext();
        JavaSparkContext sparkContext = JavaSparkContext.fromSparkContext(sparkSession.sparkContext()) ;//new JavaSparkContext(sparkConf);

        String positions = "C:\\projetos\\paulo3011\\spark-processing-samples\\latlong-processing\\src\\Data\\posicoes.csv";
        String poiFile = "C:\\projetos\\paulo3011\\spark-processing-samples\\latlong-processing\\src\\Data\\base_pois_def.csv";
        JavaRDD<String> poiFileLines = sparkContext.textFile(poiFile);
        JavaRDD<String> positionFileLines = sparkContext.textFile(positions);

        JavaRDD<PointOfInterest> poiRDD = poiFileLines.mapPartitions(new MapCsvToPointOfInterest());
        final List<PointOfInterest> pointOfInterestList = poiRDD.collect();
        Broadcast<List<PointOfInterest>> broadcastPoiRDD = sparkContext.broadcast(pointOfInterestList);

        JavaRDD<Position> positionsRDD = positionFileLines.mapPartitions(new MapCsvToPosition(broadcastPoiRDD));

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
        stoppedTimeByPlateJavaRDD.persist(StorageLevel.MEMORY_AND_DISK());

        //stoppedTimeByPlateJavaRDD.collect().forEach(summarization -> { String str = summarization.toString();  System.out.println(str); });

        JavaRDD<StoppedTimeByPlateFact> stoppedTimeByPlateFactRDD = stoppedTimeByPlateJavaRDD.mapPartitions(new MapToStoppedTimeByPlateFact());

        WriteToDisk(stoppedTimeByPlateJavaRDD,sparkSession);
    }

    public static void WriteToDisk(JavaRDD<StoppedTimeByPlate> stoppedTimeByPlateJavaRDD, SparkSession sparkSession) throws IOException {
        WriteToDiskStoppedTimeByPlateFact(stoppedTimeByPlateJavaRDD, sparkSession);
        WriteToDiskStoppedTimeByPointOfInterestFact(stoppedTimeByPlateJavaRDD, sparkSession);
    }

    public static void WriteToDiskStoppedTimeByPlateFact(JavaRDD<StoppedTimeByPlate> stoppedTimeByPlateJavaRDD, SparkSession sparkSession) throws IOException {
        JavaRDD<StoppedTimeByPlateFact> stoppedTimeByPlateFactRDD = stoppedTimeByPlateJavaRDD.mapPartitions(new MapToStoppedTimeByPlateFact());
        WriteCsvToDisk(stoppedTimeByPlateFactRDD, StoppedTimeByPlateFact.class,sparkSession,"C:\\tmp\\positions\\csv");
    }

    public static void WriteToDiskStoppedTimeByPointOfInterestFact(JavaRDD<StoppedTimeByPlate> stoppedTimeByPlateJavaRDD, SparkSession sparkSession) throws IOException {
        JavaRDD<StoppedTimeByPointOfInterestFact> stoppedTimeByPlateFactRDD = stoppedTimeByPlateJavaRDD.mapPartitions(new MapToStoppedTimeByPointOfInterestFact());
        WriteCsvToDisk(stoppedTimeByPlateFactRDD, StoppedTimeByPointOfInterestFact.class,sparkSession,"C:\\tmp\\positions\\StoppedTimeByPointOfInterestFact");
    }

    public static void WriteCsvToDisk(final JavaRDD<?> rdd, final Class<?> beanClass, SparkSession sparkSession, String outputDirPath ) throws IOException {
        final Dataset<Row> rowDataset = sparkSession.createDataFrame(rdd,beanClass);

        Path path = Paths.get(outputDirPath);

        FileUtils.deleteDirectory(path.toFile());

        rowDataset
                .repartition(1) //just for test purpose
                .write()
                .option("header","true")
                .csv(outputDirPath);
    }




}
