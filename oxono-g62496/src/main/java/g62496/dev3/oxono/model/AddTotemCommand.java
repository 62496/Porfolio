package g62496.dev3.oxono.model;

import g62496.dev3.oxono.util.Command;
/**
 * The AddTotemCommand class represents a command to add a totem to the board.
 * It encapsulates the action of adding a totem and supports undoing the action.
 */
public class AddTotemCommand implements Command {
    Board board;
    Token token;
    int dx;
    int dy;

    /**
     * Constructs an AddTotemCommand to add a totem to the board.
     *
     * @param board the board where the totem will be added
     * @param token the totem to be added
     * @param dx the x-coordinate where the totem will be placed
     * @param dy the y-coordinate where the totem will be placed
     */
    public AddTotemCommand(Board board, Token token, int dx, int dy) {
        this.board= board;
        this.token = token;
        this.dx = dx;
        this.dy= dy;
    }
    /**
     * Executes the command by adding the totem to the board at the specified position.
     *
     * This method updates the board by placing the totem at the (dx, dy) coordinates.
     */
    @Override
    public void execute() {
        board.moveTotem((Totem)token,dx,dy);
    }
    /**
     * Undoes the command by removing the totem from the board at the specified position.
     *
     * This method restores the board to its previous state by removing the totem
     * from the (dx, dy) coordinates.
     */
    @Override
    public void unexecute() {
        board.removeTotemUndo(((Totem)token).getDx(),((Totem)token).getDy());
        board.moveTotem(new Totem(((Totem) token).getType(),new Position(dx,dy)), ((Totem) token).getDx(), ((Totem) token).getDy());
    }
}
