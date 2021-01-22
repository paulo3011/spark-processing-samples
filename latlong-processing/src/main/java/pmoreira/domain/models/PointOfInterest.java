package pmoreira.domain.models;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class PointOfInterest implements Serializable {
    /**
     * Point of interest name
     */
    @Getter
    @Setter
    private String name = "";
}
