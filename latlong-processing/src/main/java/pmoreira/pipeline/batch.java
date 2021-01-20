package pmoreira.pipeline;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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

        positionsRDD.collect().forEach(position -> {
            //position.getPlate();
            System.out.println("");
        });

    }
}
