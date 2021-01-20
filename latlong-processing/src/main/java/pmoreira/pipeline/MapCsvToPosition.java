package pmoreira.pipeline;

import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.parquet.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.spark.api.java.function.FlatMapFunction;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@NoArgsConstructor
public class MapCsvToPosition
        implements FlatMapFunction<Iterator<String>, Position>
{
    @Override
    public Iterator<Position> call(Iterator<String> csvLinesPerPartition) throws Exception {
        final List<Position> result = new ObjectArrayList<>();

        while (csvLinesPerPartition.hasNext()) {
            final String textLine = csvLinesPerPartition.next();
            //converte textLine para o objeto aqui (precisa implementar a função Convert)
            Position converted = Convert(textLine);
            result.add(converted);
        }

        return result.iterator();
    }

    private Position Convert(final String textLine) throws ParseException {
        Position position = new Position();

        String jsDate="2013-3-22 10:13:00";
        Date javaDate=new SimpleDateFormat("yy-MM-dd HH:mm:ss").parse(jsDate);
        System.out.println(javaDate);

        DateFormat jsfmt = new SimpleDateFormat("EE MMM d y H:m:s 'GMT'Z (zz)");
        //Date data = jsfmt.parse(textLine);
        String separator = ",";
        String[] cols = StringUtils.splitPreserveAllTokens(textLine, separator);

        //position.setPlate(cols[0]);

        return position;
    }
}
