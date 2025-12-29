package g62496.dev3.oxono.util;

import g62496.dev3.oxono.model.AddCommand;
import g62496.dev3.oxono.model.Color;
import g62496.dev3.oxono.model.Piece;

import java.util.Stack;
/**
 * The CommandManager class is responsible for managing command execution, undo, and redo operations.
 * It maintains two stacks: one for undo operations and one for redo operations.
 * It allows executing commands, undoing the last executed command, and redoing the last undone command.
 */
public class CommandManager {

    private Stack<Command> undoStack;
    private Stack<Command> redoStack;

    /**
     * Constructs a CommandManager instance.
     * Initializes the undo and redo stacks.
     */
    public CommandManager() {
        this.undoStack =  new Stack<>();
        this.redoStack = new Stack<>();
    }
    /**
     * Executes a command and adds it to the undo stack.
     * Clears the redo stack since a new command was executed.
     *
     * @param command the command to execute
     */
    public void Do(Command command){
        command.execute();
        undoStack.add(command);
        redoStack.clear();
    }
    /**
     * Undoes the last executed command and adds it to the redo stack.
     * Stops undoing commands when a specific condition is met.
     */
    public void undo(){
        while (!undoStack.isEmpty()){
            Command command =undoStack.pop();
            command.unexecute();
            redoStack.add(command);
            if (undoStack.isEmpty() || undoStack.getLast() instanceof AddCommand && (((AddCommand) undoStack.getLast()).getToken()).getColor()== Color.BLACK){
                break;
            }
        }
    }
    /**
     * Redoes the last undone command and adds it to the undo stack.
     * Stops redoing commands when a specific condition is met.
     */
    public void redo(){
        while (!redoStack.isEmpty()){
            Command command = redoStack.pop();
            command.execute();
            undoStack.add(command);
            if (redoStack.isEmpty() || undoStack.getLast() instanceof AddCommand && ( ((AddCommand) undoStack.getLast()).getToken()).getColor()== Color.BLACK){
                break;
            }
        }
    }
}
