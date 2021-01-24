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
import org.sparkproject.guava.collect.Lists;
import pmoreira.domain.business.TimeByPlateProcessor;
import pmoreira.domain.models.PointOfInterest;
import pmoreira.domain.models.Position;
import pmoreira.domain.models.StoppedTimeByPlateFact;
import pmoreira.domain.models.StoppedTimeByPointOfInterestFact;
import pmoreira.pipeline.rddmaps.MapCsvToPointOfInterest;
import pmoreira.pipeline.rddmaps.MapCsvToPosition;
import pmoreira.pipeline.rddmaps.MapToListPositionByPlate;
import pmoreira.pipeline.rddmaps.MapToPairPlatePosition;
import pmoreira.pipeline.summarizations.MapToStoppedTimeByPlate;
import pmoreira.pipeline.summarizations.MapToStoppedTimeByPlateFact;
import pmoreira.pipeline.summarizations.MapToStoppedTimeByPointOfInterestFact;
import scala.Tuple2;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        String debugDir = "C:\\tmp\\positions\\";
        JavaRDD<String> poiFileLines = sparkContext.textFile(poiFile);
        JavaRDD<String> positionFileLines = sparkContext.textFile(positions);

        JavaRDD<PointOfInterest> poiRDD = poiFileLines.mapPartitions(new MapCsvToPointOfInterest());
        final List<PointOfInterest> pointOfInterestList = poiRDD.collect();
        Broadcast<List<PointOfInterest>> broadcastPoiRDD = sparkContext.broadcast(pointOfInterestList);

        //take each block (128mb) of the file and convert each line of csv to Position
        JavaRDD<Position> positionsRDD = positionFileLines.mapPartitions(new MapCsvToPosition(broadcastPoiRDD));
        writeTextToDisk(positionsRDD,debugDir + "positionRDD\\"); // ok

        JavaPairRDD<String,Iterable<Position>> positionListByPlateRDD = positionsRDD.groupBy(position -> position.getPlate());
        //List<Tuple2<String,Iterable<Position>>> keyPair = positionListByPlateRDD.collect();
        //ArrayList<Position> p1 = Lists.newArrayList(keyPair.get(0)._2);
        //ArrayList<Position> p2 = Lists.newArrayList(keyPair.get(1)._2); //ok

        JavaRDD<TimeByPlateProcessor> stoppedTimeByPlateJavaRDD = positionListByPlateRDD.mapPartitions(new MapToStoppedTimeByPlate());
        stoppedTimeByPlateJavaRDD.persist(StorageLevel.MEMORY_AND_DISK());
        writeToDisk(stoppedTimeByPlateJavaRDD,sparkSession);

    }

    public static void writeToDisk(JavaRDD<TimeByPlateProcessor> stoppedTimeByPlateJavaRDD, SparkSession sparkSession) throws IOException {
        writeToDiskStoppedTimeByPointOfInterestFact(stoppedTimeByPlateJavaRDD, sparkSession);
        writeToDiskStoppedTimeByPlateFact(stoppedTimeByPlateJavaRDD, sparkSession);
    }

    public static void writeToDiskStoppedTimeByPlateFact(JavaRDD<TimeByPlateProcessor> stoppedTimeByPlateJavaRDD, SparkSession sparkSession) throws IOException {
        JavaRDD<StoppedTimeByPlateFact> rdd = stoppedTimeByPlateJavaRDD.mapPartitions(new MapToStoppedTimeByPlateFact());
        writeCsvToDisk(rdd, StoppedTimeByPlateFact.class,sparkSession,"C:\\tmp\\positions\\StoppedTimeByPlateFact");
    }

    public static void writeToDiskStoppedTimeByPointOfInterestFact(JavaRDD<TimeByPlateProcessor> stoppedTimeByPlateJavaRDD, SparkSession sparkSession) throws IOException {
        JavaRDD<StoppedTimeByPointOfInterestFact> rdd = stoppedTimeByPlateJavaRDD.mapPartitions(new MapToStoppedTimeByPointOfInterestFact());
        writeCsvToDisk(rdd, StoppedTimeByPointOfInterestFact.class,sparkSession,"C:\\tmp\\positions\\StoppedTimeByPointOfInterestFact");
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
