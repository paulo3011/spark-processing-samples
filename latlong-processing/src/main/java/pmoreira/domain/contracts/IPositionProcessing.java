package pmoreira.domain.contracts;

import pmoreira.domain.models.Position;

import java.util.List;

public interface IPositionProcessing {
    /**
     * Process the next position
     * @param nextPosition
     */
    void processNextPosition(final Position nextPosition);

    /**
     * Process all positions
     * @param positionList
     */
    void processAllPosition(final Iterable<Position> positionList);
}
