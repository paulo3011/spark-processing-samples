package pmoreira.pipeline;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
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
}
