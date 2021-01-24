# Requisitos

1. Setup do java sdk para versão 15 ou superior

2. Instale o Spark e configure SPARK_HOME

3. Instale o Hadoop e configure HADOOP_HOME

4. Se windows baixe o winutils e mova para a bin do hadoop

5. Se windows reinicie a máquina

# Como executar

## Via intellij

Execute a classe BatchPositionProcessor via debug.

1. Configure os parâmetros dos arquivos de posição, poi e do diretório de saída dos arquivos do processamento

```shell script
-positions ~/tmp/position_processing/source/posicoes.csv -poi ~/tmp/position_processing/source/poi/base_pois_def.csv -output ~/tmp/position_processing/output/
```

## Via shell

```shell script
java -jar BatchPositionProcessor.jar -positions ~/tmp/position_processing/source/posicoes.csv -poi ~/tmp/position_processing/source/poi/base_pois_def.csv -output ~/tmp/position_processing/output/
```

# Referências

## Setup Apache Spark

- https://spark.apache.org/downloads.html

## Download Apache Hadoop

- https://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-common/SingleCluster.html#Download
- https://downloads.apache.org/hadoop/common/stable/

*  Spark needs a piece of Hadoop to run. For windows, you need to install winutils.exe.

## Geolocalização

- https://pt.quora.com/Como-voc%C3%AA-calcula-a-dist%C3%A2ncia-entre-duas-localiza%C3%A7%C3%B5es-geogr%C3%A1ficas-expressas-em-coordenadas-de-latitude-e-longitude-usando-Python-ou-Java
- http://jsfiddle.net/phpdeveloperrahul/rMy2B/

## Gradlew

- https://docs.gradle.org/current/userguide/java_library_plugin.html

## Lombok

- https://medium.com/collabcode/projeto-lombok-escrevendo-menos-c%C3%B3digo-em-java-8fc87b379209
- https://stackoverflow.com/questions/24006937/lombok-annotations-do-not-compile-under-intellij-idea/37840148
- https://projectlombok.org/features/GetterSetter

## Others

- https://developer.mozilla.org/pt-BR/docs/Web/JavaScript/Reference/Global_Objects/Date/getDate
- https://www.w3.org/TR/NOTE-datetime
- https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html