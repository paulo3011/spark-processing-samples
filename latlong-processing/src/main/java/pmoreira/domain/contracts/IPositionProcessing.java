package pmoreira.domain.contracts;

import pmoreira.domain.models.Position;

import java.util.List;

public interface IPositionProcessing {
    /**
     * Process the next position
     * @param nextPosition
     */
    void ProcessNextPosition(final Position nextPosition);

    /**
     * Process all positions
     * @param positionList
     */
    void ProcessAllPosition(final List<Position> positionList);
}
