package pmoreira.domain.models;

import lombok.Getter;

import java.io.Serializable;

public abstract class PositionProcessingBase implements Serializable {
    /**
     * Previous position in the current processing
     */
    @Getter
    protected Position previousPosition = null;
}
