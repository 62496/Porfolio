package g62496.dev3.oxono.util;


/**
 * The Command interface represents a generic command in the Command design pattern.
 * It defines methods for executing and unexecuting actions, allowing for undo/redo functionality.
 */
public interface Command {
     /**
      * Executes the command, performing the desired action.
      */
     void execute();
     /**
      * Unexecutes the command, reversing the action performed by {@link #execute()}.
      */
     void unexecute();
}
