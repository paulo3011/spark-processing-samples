package pmoreira.domain.business;

import org.junit.Test;
import pmoreira.domain.models.PointOfInterest;
import pmoreira.domain.models.Position;
import pmoreira.domain.models.TimeByPoi;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class TimeByPlateProcessorTest {

    private final String poiAtStartId = "Deposito inicial";
    private final String poiAtEndId = "Deposito final";

    /**
     * The processing needs to calc:
     * - 120s stopped regardless of the point
     * - 0s inside and stopped at endPoi
     * - 120s inside at endPoi
     */
    private Iterable<Position> buildPrdAgostinhoTrip1(String plate) {
        //https://docs.oracle.com/javase/8/docs/api/java/util/ArrayList.html
        ArrayList<Position> trip = new ArrayList<>();
        LocalDateTime lastDate = LocalDateTime.now();
        long increment = 2;
        boolean ignitionOn = true;
        boolean ignitionOff = false;

        // https://www.google.com.br/maps/dir/-25.4246219,-49.2819837/-25.4248454,-49.2824707/-25.4250198,-49.2830393/-25.4253008,-49.2836616/-25.4261341,-49.2843912/@-25.4252863,-49.2836616,17z/data=!4m2!4m1!3e0
        PointOfInterest poiAtEnd = new PointOfInterest(poiAtEndId,-25.425950011685394, -49.284369693684866, 220);

        //0s
        trip.add(new Position(plate, lastDate, -25.424651591511754, -49.28198789211437, 0, ignitionOff)); //stopped
        lastDate = lastDate.plusMinutes(increment);
        //120s
        trip.add(new Position(plate, lastDate, -25.424651591511754, -49.28198789211437, 0, ignitionOff)); //120s stopped regardless poi
        lastDate = lastDate.plusMinutes(increment);
        //240s
        trip.add(new Position(plate, lastDate, -25.4248453864594, -49.28244923206236, 5, ignitionOn)); //engine on, not stopped
        lastDate = lastDate.plusMinutes(increment);
        //360s
        trip.add(new Position(plate, lastDate, -25.425039181095464, -49.28302858920635, 10, ignitionOn, poiAtEnd)); //running, 0s inside poiAtEnd
        lastDate = lastDate.plusMinutes(increment);
        //480s
        trip.add(new Position(plate, lastDate, -25.425310493062447, -49.283661590530336, 20, ignitionOn, poiAtEnd)); //running, 120s inside poiAtEnd

        return trip;
    }

    /**
     * The processing needs to calc:
     * - 120s stopped and inside at startPoi
     * - 0s inside and stopped at endPoi
     * - 120s inside at endPoi
     * - 120s stopped regardless of the point
     * @param plate Plate who belongs the trip
     * @return Iterable<Position>
     */
    private Iterable<Position> buildPrdAgostinhoTrip2(String plate) {
        ArrayList<Position> trip = new ArrayList<>();
        LocalDateTime lastDate = LocalDateTime.now();
        long increment = 2;
        boolean ignitionOn = true;
        boolean ignitionOff = false;

        PointOfInterest poiAtStart = new PointOfInterest(poiAtStartId,-25.42357602386308, -49.280882821984925, 100);

        // https://www.google.com.br/maps/dir/-25.4246219,-49.2819837/-25.4248454,-49.2824707/-25.4250198,-49.2830393/-25.4253008,-49.2836616/-25.4261341,-49.2843912/@-25.4252863,-49.2836616,17z/data=!4m2!4m1!3e0
        PointOfInterest poiAtEnd = new PointOfInterest(poiAtEndId,-25.425950011685394, -49.284369693684866, 220);

        //0s
        trip.add(new Position(plate, lastDate, -25.424651591511754, -49.28198789211437, 0, ignitionOff, poiAtStart));
        lastDate = lastDate.plusMinutes(increment);
        //120s
        trip.add(new Position(plate, lastDate, -25.424651591511754, -49.28198789211437, 0, ignitionOff, poiAtStart)); //120s stopped
        lastDate = lastDate.plusMinutes(increment);
        //240s
        trip.add(new Position(plate, lastDate, -25.4248453864594, -49.28244923206236, 5, ignitionOn)); //not stopped and out poi
        lastDate = lastDate.plusMinutes(increment);
        //360s
        trip.add(new Position(plate, lastDate, -25.425039181095464, -49.28302858920635, 10, ignitionOn, poiAtEnd)); //running
        lastDate = lastDate.plusMinutes(increment);
        //480s
        trip.add(new Position(plate, lastDate, -25.425310493062447, -49.283661590530336, 20, ignitionOn, poiAtEnd)); //120s running

        return trip;
    }

    @Test
    public void processPrdAgostinhoTrip1() {
        TimeByPlateProcessor processar = new TimeByPlateProcessor();
        processar.setPlate("ATC4501");
        processar.processAllPosition(this.buildPrdAgostinhoTrip1(processar.getPlate()));

        //The processing needs to calc:
        TimeByPoi timeByPoiAtEnd = processar.getTimeByPoi().get(poiAtEndId);
        // 120s stopped regardless of the point
        assertEquals(120, processar.getTotalStoppedTime().getSum(), 0.0);
        // 120s inside at endPoi
        assertEquals(120, timeByPoiAtEnd.getTotalSecondsInsidePoi().getSum(), 0.0);
        // 0s inside and stopped at endPoi
        assertEquals(0, timeByPoiAtEnd.getTotalSecondsStoppedInsidePoi().getSum(), 0.0);
     }

    @Test
    public void processPrdAgostinhoTrip2() {
        TimeByPlateProcessor processar = new TimeByPlateProcessor();
        processar.setPlate("ATC4502");
        processar.processAllPosition(this.buildPrdAgostinhoTrip2(processar.getPlate()));

        /*
         * The processing needs to calc:
         * - 120s stopped and inside at startPoi
         * - 0s inside and stopped at endPoi
         * - 120s inside at endPoi
         * - 120s stopped regardless of the point
         */

        assertEquals(120, processar.getTotalStoppedTime().getSum(), 0.0);

        TimeByPoi timeByPoiAtStart = processar.getTimeByPoi().get(poiAtStartId);
        assertEquals(120, timeByPoiAtStart.getTotalSecondsInsidePoi().getSum(), 0.0);
        assertEquals(120, timeByPoiAtStart.getTotalSecondsStoppedInsidePoi().getSum(), 0.0);

        TimeByPoi timeByPoiAtEnd = processar.getTimeByPoi().get(poiAtEndId);
        assertEquals(120, timeByPoiAtEnd.getTotalSecondsInsidePoi().getSum(), 0.0);
        assertEquals(0, timeByPoiAtEnd.getTotalSecondsStoppedInsidePoi().getSum(), 0.0);
    }

    /**
     * The processing needs to calc:
     * - 480s inside at poiAtStartId
     * - 120s stopped and inside at poiAtStartId
     * - 120s stopped regardless of the point
     * @param plate Plate
     */
    private Iterable<Position> buildPrdAgostinhoTrip3(String plate) {
        ArrayList<Position> trip = new ArrayList<>();
        LocalDateTime lastDate = LocalDateTime.now();
        long increment = 2;
        boolean ignitionOn = true;
        boolean ignitionOff = false;

        // https://www.google.com.br/maps/dir/-25.4246219,-49.2819837/-25.4248454,-49.2824707/-25.4250198,-49.2830393/-25.4253008,-49.2836616/-25.4261341,-49.2843912/@-25.4252863,-49.2836616,17z/data=!4m2!4m1!3e0

        //all positions are inside poi
        //time inside:
        PointOfInterest poiAtStart = new PointOfInterest(poiAtStartId,-25.42357602386308, -49.280882821984925, 2000);

        //0s stopped and inside
        trip.add(new Position(plate, lastDate, -25.424651591511754, -49.28198789211437, 0, ignitionOff, poiAtStart));
        lastDate = lastDate.plusMinutes(increment);
        //120s inside
        //120s stopped
        trip.add(new Position(plate, lastDate, -25.424651591511754, -49.28198789211437, 0, ignitionOff, poiAtStart));
        lastDate = lastDate.plusMinutes(increment);
        //did not run according to the rule: speed < 5 && ignition==false, but needs to be sum as time inside
        //240s inside
        //120s stopped (ignition is not false, don't sum)
        trip.add(new Position(plate, lastDate, -25.4248453864594, -49.28244923206236, 4, ignitionOn, poiAtStart));
        lastDate = lastDate.plusMinutes(increment);
        //360s inside
        //120s stopped (ignition is not false, don't sum)
        //120s running
        trip.add(new Position(plate, lastDate, -25.425039181095464, -49.28302858920635, 10, ignitionOn, poiAtStart));
        lastDate = lastDate.plusMinutes(increment);
        //480s inside
        //120s stopped (ignition is not false, don't sum)
        //240s running
        trip.add(new Position(plate, lastDate, -25.425310493062447, -49.283661590530336, 20, ignitionOn, poiAtStart));

        return trip;
    }

    /**
     * The processing needs to calc:
     * - 480s inside at poiAtStartId
     * - 120s stopped and inside at poiAtStartId
     * - 240s stopped regardless of the point * added 120s here
     * @param plate Plate
     */
    private Iterable<Position> buildPrdAgostinhoTrip4(String plate) {
        long increment = 2;
        boolean ignitionOff = false;

        //480s inside
        //120s stopped (ignition is not false, don't sum)
        //240s running
        ArrayList<Position> trip = (ArrayList<Position>)this.buildPrdAgostinhoTrip3(plate);
        LocalDateTime lastDate = trip.get(4).getPositionDate();
        lastDate = lastDate.plusMinutes(increment);

        //480s inside (out of poi)
        //120s stopped (ignition is not false, don't sum)
        //240s stopped regardless of the point
        //360s running
        //R. Maj. França Gomes, 999-901 - Santa Quiteria, Curitiba - PR, 80310-000
        trip.add(new Position(plate, lastDate, -25.458950798434934, -49.31234904705773, 0, ignitionOff));

        return trip;
    }

    @Test
    public void processPrdAgostinhoTrip3() {
        TimeByPlateProcessor processar = new TimeByPlateProcessor();
        processar.setPlate("ATC4503");
        processar.processAllPosition(this.buildPrdAgostinhoTrip3(processar.getPlate()));
        /*
         * The processing needs to calc:
         * - 480s inside at poiAtStartId
         * - 120s stopped and inside at poiAtStartId
         * - 120s stopped regardless of the point
         */

        double insideAtPoiStart = 480;
        double stoppedAtPoiStart = 120;
        double stoppedRegardlessPoi = 120;

        TimeByPoi timeByPoiAtStart = processar.getTimeByPoi().get(poiAtStartId);

        assertEquals(processar.getTotalStoppedTime().getSum(), stoppedRegardlessPoi, 0.0);
        assertEquals(timeByPoiAtStart.getTotalSecondsInsidePoi().getSum(), insideAtPoiStart, 0.0);
        assertEquals(timeByPoiAtStart.getTotalSecondsStoppedInsidePoi().getSum(), stoppedAtPoiStart, 0.0);
    }

    @Test
    public void processPrdAgostinhoTrip4() {
        TimeByPlateProcessor processar = new TimeByPlateProcessor();
        processar.setPlate("ATC4504");
        processar.processAllPosition(this.buildPrdAgostinhoTrip4(processar.getPlate()));
        /*
         * The processing needs to calc:
         * - 480s inside at poiAtStartId
         * - 120s stopped and inside at poiAtStartId
         * - ** 240s stopped regardless of the point
         */
        double insideAtPoiStart = 480;
        double stoppedAtPoiStart = 120;
        double stoppedRegardlessPoi = 240;

        TimeByPoi timeByPoiAtStart = processar.getTimeByPoi().get(poiAtStartId);

        assertEquals(processar.getTotalStoppedTime().getSum(), stoppedRegardlessPoi, 0.0);
        assertEquals(timeByPoiAtStart.getTotalSecondsInsidePoi().getSum(), insideAtPoiStart, 0.0);
        assertEquals(timeByPoiAtStart.getTotalSecondsStoppedInsidePoi().getSum(), stoppedAtPoiStart, 0.0);
    }
}