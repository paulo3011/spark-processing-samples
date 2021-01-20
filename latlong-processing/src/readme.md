# Requisitos

1. Setup java sdk para versão 15 ou superior

2. Configurar dependências no gradlew

```groovy
dependencies {
    testCompile group: 'junit', name: 'junit', version: '4.12'
    // https://mvnrepository.com/artifact/org.apache.spark/spark-core
    compile group: 'org.apache.spark', name: 'spark-core_2.12', version: '3.1.0'
    // https://mvnrepository.com/artifact/org.apache.spark/spark-sql
    compile group: 'org.apache.spark', name: 'spark-sql_2.12', version: '3.1.0'
}
```

3. Adicionar referências para classes do spark
```java
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.SparkConf;
```

# Referências

## Setup spark

- https://spark.apache.org/docs/latest/rdd-programming-guide.html#overview
- https://medium.com/@eyaldahari/how-to-run-apache-spark-with-hadoop-using-intellij-on-windows-359a8421507b
- https://mvnrepository.com/artifact/org.apache.spark/spark-core_2.12/3.1.0
- https://spark.apache.org/examples.html

### Download hadoop

- https://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-common/SingleCluster.html#Download
- https://downloads.apache.org/hadoop/common/stable/

## Geolocalização

- https://pt.quora.com/Como-voc%C3%AA-calcula-a-dist%C3%A2ncia-entre-duas-localiza%C3%A7%C3%B5es-geogr%C3%A1ficas-expressas-em-coordenadas-de-latitude-e-longitude-usando-Python-ou-Java
- http://jsfiddle.net/phpdeveloperrahul/rMy2B/

## Gradlew

- https://projectlombok.org/features/GetterSetter

## Java

- https://javamex.com/tutorials/synchronization_final.shtml
- https://www.educative.io/edpresso/what-is-the-final-keyword-in-java
- https://softwareengineering.stackexchange.com/questions/98691/excessive-use-final-keyword-in-java

### Lombok

- https://medium.com/collabcode/projeto-lombok-escrevendo-menos-c%C3%B3digo-em-java-8fc87b379209
- https://stackoverflow.com/questions/24006937/lombok-annotations-do-not-compile-under-intellij-idea/37840148

## Others

- https://developer.mozilla.org/pt-BR/docs/Web/JavaScript/Reference/Global_Objects/Date/getDate
- https://www.w3.org/TR/NOTE-datetime