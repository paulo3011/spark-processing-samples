import com.grum.geocalc.BoundingArea;
import com.grum.geocalc.Coordinate;
import com.grum.geocalc.EarthCalc;
import com.grum.geocalc.Point;
import org.junit.Test;

import static org.junit.Assert.*;

public class GeocalclTest {
    @Test
    public void SampleCalc(){
        //seealso: https://github.com/grumlimited/geocalc
        /*
        //geopy - return 7300.209660192641 meters
        p1 = (-23.5842844, -46.6610056)
        p2 = (-23.6388952, -46.6209493)
        d = geopy.distance.distance(p1, p2)
        */
        Coordinate lat = Coordinate.fromDegrees(-23.5842844);
        Coordinate lng = Coordinate.fromDegrees(-46.6610056);
        Point p1 = Point.at(lat, lng);
        lat = Coordinate.fromDegrees(-23.6388952);
        lng = Coordinate.fromDegrees(-46.6209493);
        Point p2 = Point.at(lat, lng);
        double distancePip2 = EarthCalc.gcd.distance(p1, p2); //7300.094235982657

        //R. Padre Agostinho, 479-361 - São Francisco, Curitiba - PR, 80430-050
        //-25.425445549426303, -49.283993285365014

        //R. Padre Agostinho, 3081-3051 - São Francisco, Curitiba - PR
        //-25.43501858423085, -49.307897131972624
        double distancePadreAgostinho = CalcDistanceBetween(-25.425445549426303, -49.283993285365014, -25.43501858423085, -49.307897131972624);
        assertEquals(2620, (int) distancePadreAgostinho);
        //return 2620.0118586130384 meters
        //same as https://www.google.com.br/maps/dir/-25.4255174,-49.2842061/-25.4349728,-49.3078788/@-25.4302322,-49.3048072,15z/data=!3m1!4b1!4m2!4m1!3e0
    }

    @Test
    public void IsInsidePointTest()
    {
        //R. Padre Agostinho, 479-361 - São Francisco, Curitiba - PR, 80430-050
        Coordinate lat = Coordinate.fromDegrees(-25.425445549426303);
        Coordinate lng = Coordinate.fromDegrees(-49.283993285365014 );
        Point p1 = Point.at(lat, lng);

        //R. Padre Agostinho, 3081-3051 - São Francisco, Curitiba - PR
        lat = Coordinate.fromDegrees(-25.43501858423085);
        lng = Coordinate.fromDegrees(-49.307897131972624);
        Point p2 = Point.at(lat, lng);

        //R. Gen. Mário Tourinho - Santo Inacio, Curitiba - PR
        //-25.431607257500126, -49.31467786499901
        lat = Coordinate.fromDegrees(-25.431607257500126);
        lng = Coordinate.fromDegrees(-49.31467786499901);
        Point poi = Point.at(lat, lng);

        BoundingArea boundingArea = EarthCalc.gcd.around(poi,1000);

        //https://www.google.com.br/maps/dir/-25.4255174,-49.2842061/-25.4349728,-49.3078788/-25.4316848,-49.3145062/@-25.4302508,-49.3135621,14z/data=!4m2!4m1!3e0
        boolean p2IsInsidePoi = boundingArea.contains(p2);
        boolean p1IsOutsidePoi = boundingArea.contains(p1);
        assertTrue(p2IsInsidePoi); //needs to be True
        assertFalse(p1IsOutsidePoi); //needs to be False
    }

    public double CalcDistanceBetween(double latitude, double longitude, double latitude2, double longitude2){
        Coordinate lat = Coordinate.fromDegrees(latitude);
        Coordinate lng = Coordinate.fromDegrees(longitude);
        final Point p1 = Point.at(lat, lng);
        lat = Coordinate.fromDegrees(latitude2);
        lng = Coordinate.fromDegrees(longitude2);
        final Point p2 = Point.at(lat, lng);
        return EarthCalc.gcd.distance(p1, p2);
    }
}
