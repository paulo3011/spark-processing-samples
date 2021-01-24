package pmoreira.pipeline;

import org.apache.commons.io.FileUtils;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.SparkConf;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.storage.StorageLevel;
import pmoreira.domain.business.TimeByPlateProcessor;
import pmoreira.domain.models.*;
import pmoreira.pipeline.rddmaps.MapCsvToPointOfInterest;
import pmoreira.pipeline.rddmaps.MapCsvToPosition;
import pmoreira.pipeline.summarizations.MapToStoppedTimeByPoiFact;
import pmoreira.pipeline.summarizations.MapToTimeByPlateProcessor;
import pmoreira.pipeline.rddmaps.MapToStoppedTimeByPlateFact;
import pmoreira.pipeline.rddmaps.MapToTimeByPoiAndPlateFact;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class batch {
    public static void main(String[] args) throws IOException {
        System.out.println("Starting batch processing");

        /*
          The first thing a Spark program must do is to create a JavaSparkContext object, which tells Spark how to access a cluster.
          To create a SparkContext you first need to build a SparkConf object that contains information about your application.
         */
        SparkConf sparkConf = new SparkConf()
                .setAppName("PositionProcessing")
                .setMaster("local[*]");  // Delete this line when submitting to a cluster

        SparkSession sparkSession = SparkSession
                .builder()
                .config(sparkConf)
                .getOrCreate();

        JavaSparkContext sparkContext = JavaSparkContext.fromSparkContext(sparkSession.sparkContext()) ;//new JavaSparkContext(sparkConf);

        String positions = "C:\\projetos\\paulo3011\\spark-processing-samples\\latlong-processing\\src\\Data\\posicoes.csv";
        String poiFile = "C:\\projetos\\paulo3011\\spark-processing-samples\\latlong-processing\\src\\Data\\base_pois_def.csv";
        //String debugDir = "C:\\tmp\\positions\\";
        JavaRDD<String> poiFileLines = sparkContext.textFile(poiFile);
        JavaRDD<String> positionFileLines = sparkContext.textFile(positions);

        JavaRDD<PointOfInterest> poiRDD = poiFileLines.mapPartitions(new MapCsvToPointOfInterest());
        final List<PointOfInterest> pointOfInterestList = poiRDD.collect();
        Broadcast<List<PointOfInterest>> broadcastPoiRDD = sparkContext.broadcast(pointOfInterestList);

        //take each block (128mb) of the file and convert each line of csv to Position
        JavaRDD<Position> positionsRDD = positionFileLines.mapPartitions(new MapCsvToPosition(broadcastPoiRDD));
        //writeTextToDisk(positionsRDD,debugDir + "positionRDD\\"); // ok

        JavaPairRDD<String,Iterable<Position>> positionListByPlateRDD = positionsRDD.groupBy(Position::getPlate);

        JavaRDD<TimeByPlateProcessor> timeByPlateRDD = positionListByPlateRDD.mapPartitions(new MapToTimeByPlateProcessor());
        timeByPlateRDD.persist(StorageLevel.MEMORY_AND_DISK());

        writeToDisk(timeByPlateRDD,sparkSession);
    }

    public static void writeToDisk(JavaRDD<TimeByPlateProcessor> timeByPlateRDD, SparkSession sparkSession) throws IOException {
        writeToDiskTimeByPoiAndPlateFact(timeByPlateRDD, sparkSession);
        writeToDiskStoppedTimeByPlateFact(timeByPlateRDD, sparkSession);
    }

    public static void writeToDiskStoppedTimeByPlateFact(JavaRDD<TimeByPlateProcessor> timeByPlateRDD, SparkSession sparkSession) throws IOException {
        JavaRDD<StoppedTimeByPlateFact> rdd = timeByPlateRDD.mapPartitions(new MapToStoppedTimeByPlateFact());
        writeCsvToDisk(rdd, StoppedTimeByPlateFact.class,sparkSession,"C:\\tmp\\positions\\StoppedTimeByPlateFact");
    }

    public static void writeToDiskTimeByPoiAndPlateFact(JavaRDD<TimeByPlateProcessor> timeByPlateRDD, SparkSession sparkSession) throws IOException {
        JavaRDD<TimeByPoiAndPlateFact> timeByPoiAndPlateFactRDD = timeByPlateRDD.mapPartitions(new MapToTimeByPoiAndPlateFact());
        writeCsvToDisk(timeByPoiAndPlateFactRDD, TimeByPoiAndPlateFact.class,sparkSession,"C:\\tmp\\positions\\TimeByPoiAndPlateFact");
        timeByPoiAndPlateFactRDD.persist(StorageLevel.MEMORY_AND_DISK());

        JavaPairRDD<String,Iterable<TimeByPoiAndPlateFact>> timeByPoiRDD = timeByPoiAndPlateFactRDD.groupBy(TimeByPoiAndPlateFact::getPointOfInterest);
        JavaRDD<StoppedTimeByPoiFact> timeByPoiFactDD = timeByPoiRDD.mapPartitions(new MapToStoppedTimeByPoiFact());
        writeCsvToDisk(timeByPoiFactDD, StoppedTimeByPoiFact.class,sparkSession,"C:\\tmp\\positions\\StoppedTimeByPoiFact");
    }

    public static void writeCsvToDisk(final JavaRDD<?> rdd, final Class<?> beanClass, SparkSession sparkSession, String outputDirPath ) throws IOException {
        final Dataset<Row> rowDataset = sparkSession.createDataFrame(rdd,beanClass);

        Path path = Paths.get(outputDirPath);

        FileUtils.deleteDirectory(path.toFile());

        rowDataset
                .repartition(1) //just for test purpose
                .write()
                .option("header","true")
                .csv(outputDirPath);
    }

    public static void writeTextToDisk(final JavaRDD<?> rdd, String outputDirPath) throws IOException {
        Path path = Paths.get(outputDirPath);
        FileUtils.deleteDirectory(path.toFile());
        rdd.repartition(1).saveAsTextFile(outputDirPath);
    }




}
