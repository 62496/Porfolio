package g62496.dev3.oxono.util;

import g62496.dev3.oxono.model.Position;

import java.util.List;
/**
 * The Observer interface defines the contract for an object that receives updates from an observable.
 * It allows the observer to be notified when changes occur in the observable object.
 */
public interface Observer {
    /**
     * Updates the observer with the provided list of positions and a message.
     *
     * @param positionList the list of positions to update the observer with
     * @param message the message containing additional information for the observer
     */
    void update(List<Position>positionList,String message);
}

