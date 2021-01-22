package pmoreira.domain.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

@NoArgsConstructor
public class Position
        implements Serializable
{
    /**
     * Set or get Vehicle plate
     */
    @Getter @Setter
    private String plate;
    /**
     * Set or get the date when it was received the position
     */
    @Getter @Setter
    private LocalDateTime positionDate;

    /**
     * Set or get the vehicle speed at the position
     */
    @Getter @Setter
    private Integer speed;

    @Getter @Setter
    private float latitude;

    @Getter @Setter
    private float longitude;

    /**
     * Set or get if the vehicle ignition is on or off.
     * True on and False off.
     */
    @Getter @Setter
    private boolean ignition;

    /**
     * Return true if vehicle is stopped or false otherwise
     * @return
     */
    public boolean isStopped()
    {
        if(speed < 5 && ignition == false)
            return true;
        return false;
    }

    /**
     * Return the difference between next position and this position.
     * @param nextPosition
     * @return
     */
    public long getSecondsFromNext(final Position nextPosition)
    {
        Duration duration = Duration.between(this.positionDate, nextPosition.positionDate);
        return duration.getSeconds();
    }

    /**
     * Logic to order position list by plate and position date.
     * @param position1
     * @param position2
     * @return
     */
    public static int orderByPositionDate(final Position position1, final Position position2)
    {
        int lastComparison = 0;
        lastComparison = position1.getPlate().compareTo(position2.getPlate());

        if(lastComparison == 0){
            lastComparison = position1.getPositionDate().compareTo(position2.getPositionDate());
        }

        return lastComparison;
    }

}
