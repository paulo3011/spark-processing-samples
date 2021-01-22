import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import pmoreira.pipeline.MapCsvToPosition;
import pmoreira.domain.models.Position;

import static org.junit.Assert.*;

public class JsDateParseTest {
    @Test
    public void TestParseDate() throws ParseException {
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

        String pattern = "EEE MMM d yyyy HH:mm:ss 'GMT'Z";
        String stringToRemove = " (Hora oficial do Brasil)";
        String jsDateSample = "Wed Dec 12 2018 00:04:03 GMT-0200 (Hora oficial do Brasil)";

        String[] cols = StringUtils.split(jsDateSample," (");
        String jsDate = StringUtils.remove(jsDateSample,stringToRemove);
        DateFormat jsfmt = new SimpleDateFormat(pattern, Locale.ENGLISH);
        Date data = jsfmt.parse(jsDate);
        String inputDate = jsfmt.format(data);

        assertEquals(inputDate, jsDate);
    }

    @Test
    public void TestParseCols() throws ParseException {
        String textLine="TESTE001,Wed Dec 12 2018 00:04:03 GMT-0200 (Hora oficial do Brasil),0,-51.469891,-25.3649141,false";
        String separator = ",";
        String[] cols = StringUtils.splitPreserveAllTokens(textLine, separator);
        String plate = cols[0];
        String strDate = cols[1];
        String strSpeed = cols[2];
        String strLongitude = cols[3];
        String strLatitude = cols[4];
        String strIgnition = cols[5];

        MapCsvToPosition parser = new MapCsvToPosition();

        int speed = Integer.parseInt(strSpeed);
        LocalDateTime dta = parser.ParsePositionDate(strDate);
        float lat = Float.parseFloat(strLatitude);
        float longitude = Float.parseFloat(strLongitude);
        boolean ign = Boolean.parseBoolean(strIgnition);

        Position position = new Position();
        position.setLatitude(lat);
        position.setLongitude(longitude);
        position.setIgnition(ign);
        position.setSpeed(speed);
        position.setPositionDate(dta);
    }
}
