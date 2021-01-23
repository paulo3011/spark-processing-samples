package pmoreira.pipeline;

import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.parquet.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.spark.api.java.function.FlatMapFunction;
import pmoreira.domain.models.PointOfInterest;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

@NoArgsConstructor
public class MapCsvToPointOfInterest
        implements FlatMapFunction<Iterator<String>, PointOfInterest>, Serializable
{
    @Override
    public Iterator<PointOfInterest> call(Iterator<String> csvLinesPerPartition) throws Exception {
        System.out.println("starting convert csv line to PointOfInterest");

        final List<PointOfInterest> result = new ObjectArrayList<>();

        while (csvLinesPerPartition.hasNext()) {
            final String textLine = csvLinesPerPartition.next();

            if(this.isValid(textLine)==false)
                continue;

            try {
                PointOfInterest converted = Convert(textLine);
                result.add(converted);
            }
            catch (Exception ex){
                System.out.println(textLine);
                System.out.println(ex);
            }
        }

        return result.iterator();
    }

    private boolean isValid(String textLine) {
        if(textLine == null || textLine.startsWith("nome"))
            return false;
        return true;
    }

    private PointOfInterest Convert(final String textLine) throws ParseException {
        PointOfInterest PointOfInterest = new PointOfInterest();

        String separator = ",";
        String[] cols = StringUtils.splitPreserveAllTokens(textLine, separator);

        final String name = cols[0];
        final float radius = Float.parseFloat(cols[1]);
        final float latitude = Float.parseFloat(cols[2]);
        final float longitude = Float.parseFloat(cols[3]);

        PointOfInterest.setName(name);
        PointOfInterest.setRadius(radius);
        PointOfInterest.setLongitude(longitude);
        PointOfInterest.setLatitude(latitude);

        return PointOfInterest;
    }
}
