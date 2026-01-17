package g62496.dev3.oxono.util;

import g62496.dev3.oxono.model.Position;

import java.util.List;
/**
 * The Observable interface defines the contract for an object that can be observed by other objects.
 * It allows observers to register, unregister, and be notified of changes in the state of the observable.
 */
public interface Observable {
    /**
     * Registers an observer to receive notifications from this observable.
     *
     * @param o the observer to register
     */
    void registerObserver(Observer o);
    /**
     * Removes an observer from the list of observers for this observable.
     *
     * @param o the observer to remove
     */
    void removeObserver(Observer o);
    /**
     * Notifies all registered observers with a list of positions and a message.
     *
     * @param positionList the list of positions to be sent to observers
     * @param message the message to be sent to observers
     */
    void notifyObservers(List<Position>positionList,String message);
}
