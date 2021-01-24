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
import pmoreira.domain.models.PointOfInterest;
import pmoreira.domain.models.Position;
import pmoreira.domain.models.StoppedTimeByPlateFact;
import pmoreira.domain.models.StoppedTimeByPointOfInterestFact;

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
        JavaRDD<String> poiFileLines = sparkContext.textFile(poiFile);
        JavaRDD<String> positionFileLines = sparkContext.textFile(positions);

        JavaRDD<PointOfInterest> poiRDD = poiFileLines.mapPartitions(new MapCsvToPointOfInterest());
        final List<PointOfInterest> pointOfInterestList = poiRDD.collect();
        Broadcast<List<PointOfInterest>> broadcastPoiRDD = sparkContext.broadcast(pointOfInterestList);

        JavaRDD<Position> positionsRDD = positionFileLines.mapPartitions(new MapCsvToPosition(broadcastPoiRDD));
        JavaPairRDD<String,Position> positionByPlatePairRDD = positionsRDD.mapPartitionsToPair(new MapToPairPlatePosition());
        JavaPairRDD<String,List<Position>> positionListByPlateRDD = new MapToListPositionByPlate().groupPositionsByPlate(positionByPlatePairRDD);

        JavaRDD<StoppedTimeByPlate> stoppedTimeByPlateJavaRDD = positionListByPlateRDD.mapPartitions(new MapToStoppedTimeByPlate());
        stoppedTimeByPlateJavaRDD.persist(StorageLevel.MEMORY_AND_DISK());

        WriteToDisk(stoppedTimeByPlateJavaRDD,sparkSession);
    }

    public static void WriteToDisk(JavaRDD<StoppedTimeByPlate> stoppedTimeByPlateJavaRDD, SparkSession sparkSession) throws IOException {
        WriteToDiskStoppedTimeByPointOfInterestFact(stoppedTimeByPlateJavaRDD, sparkSession);
        WriteToDiskStoppedTimeByPlateFact(stoppedTimeByPlateJavaRDD, sparkSession);
    }

    public static void WriteToDiskStoppedTimeByPlateFact(JavaRDD<StoppedTimeByPlate> stoppedTimeByPlateJavaRDD, SparkSession sparkSession) throws IOException {
        JavaRDD<StoppedTimeByPlateFact> rdd = stoppedTimeByPlateJavaRDD.mapPartitions(new MapToStoppedTimeByPlateFact());
        WriteCsvToDisk(rdd, StoppedTimeByPlateFact.class,sparkSession,"C:\\tmp\\positions\\StoppedTimeByPlateFact");
    }

    public static void WriteToDiskStoppedTimeByPointOfInterestFact(JavaRDD<StoppedTimeByPlate> stoppedTimeByPlateJavaRDD, SparkSession sparkSession) throws IOException {
        JavaRDD<StoppedTimeByPointOfInterestFact> rdd = stoppedTimeByPlateJavaRDD.mapPartitions(new MapToStoppedTimeByPointOfInterestFact());
        WriteCsvToDisk(rdd, StoppedTimeByPointOfInterestFact.class,sparkSession,"C:\\tmp\\positions\\StoppedTimeByPointOfInterestFact");
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
