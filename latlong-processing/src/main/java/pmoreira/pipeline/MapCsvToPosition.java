package pmoreira.pipeline;

import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.parquet.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.broadcast.Broadcast;
import pmoreira.domain.models.PointOfInterest;
import pmoreira.domain.models.Position;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;


public class MapCsvToPosition
        implements FlatMapFunction<Iterator<String>, Position>, Serializable
{
    public MapCsvToPosition(){

    }

    public MapCsvToPosition(Broadcast<List<PointOfInterest>> broadcastPoiRDD){
        this.broadcastPoiRDD = broadcastPoiRDD;
    }

    /**
     * List of point of interest
     */
    private Broadcast<List<PointOfInterest>> broadcastPoiRDD;

    @Override
    public Iterator<Position> call(Iterator<String> csvLinesPerPartition) throws Exception {
        final List<Position> result = new ObjectArrayList<>();

        while (csvLinesPerPartition.hasNext()) {
            final String textLine = csvLinesPerPartition.next();

            if(this.isValid(textLine)==false)
                continue;

            try {
                Position converted = Convert(textLine);
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
        if(textLine == null || textLine.startsWith("placa"))
            return false;
        return true;
    }

    private Position Convert(final String textLine) throws ParseException {
        Position position = new Position();
        System.out.println("starting convert csv line to Position");

        String separator = ",";
        String[] cols = StringUtils.splitPreserveAllTokens(textLine, separator);

        final String plate = cols[0];
        final LocalDateTime date = this.ParsePositionDate(cols[1]);
        final int speed = Integer.parseInt(cols[2]);
        final float longitude = Float.parseFloat(cols[3]);
        final float latitude = Float.parseFloat(cols[4]);
        final boolean ignition = Boolean.parseBoolean(cols[5]);

        position.setPlate(plate);
        position.setPositionDate(date);
        position.setSpeed(speed);
        position.setLongitude(longitude);
        position.setLatitude(latitude);
        position.setIgnition(ignition);
        position.setNearestPointOfInterest(this.findNearestPointOfInterest(position));

        return position;
    }

    private PointOfInterest findNearestPointOfInterest(Position position) {
        if(this.broadcastPoiRDD == null)
            return null;

        for(final PointOfInterest pointOfInterest: this.broadcastPoiRDD.getValue()){
            if(position.isInsidePointOfInterest(pointOfInterest)){
                return pointOfInterest;
            }
        }

        return null;
    }

    public LocalDateTime convertToLocalDateTimeFromMilisecond(Date dateToConvert) {
        return Instant.ofEpochMilli(dateToConvert.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    /**
     * Parse postion string date
     * @param csvPositionDate Java script string date
     * @return Returns a LocalDateTime
     * @throws ParseException
     */
    public LocalDateTime ParsePositionDate(String csvPositionDate) throws ParseException {
        /*
        SimpleDateFormat dateFormatOfStringInDB = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date d1 = dateFormatOfStringInDB.parse("12/12/2018 00:04:03");
        SimpleDateFormat dateFormatYouWant = new SimpleDateFormat("EEE MMM d yyyy HH:mm:ss 'GMT'Z (z)", Locale.ENGLISH);
        String sCertDate = dateFormatYouWant.format(d1); // => Wed Dec 12 2018 00:04:03 GMT-0200 (BRST)
        */

        /*
        Wed Dec 12 2018 00:04:03 GMT-0200 (Hora oficial do Brasil)
        EEE => Day name in week, e.g. Wed
        MMM => Month, e.g. Dec
        yyyy => year
        */

        final String pattern = "EEE MMM d yyyy HH:mm:ss 'GMT'Z";
        final String stringToRemove = " (Hora oficial do Brasil)";
        //String jsDateSample = "Wed Dec 12 2018 00:04:03 GMT-0200 (Hora oficial do Brasil)";

        String jsDate = StringUtils.remove(csvPositionDate,stringToRemove);
        DateFormat jsDateFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);
        Date data = jsDateFormat.parse(jsDate);

        final LocalDateTime localDate = this.convertToLocalDateTimeFromMilisecond(data);
        return localDate;
    }
}
