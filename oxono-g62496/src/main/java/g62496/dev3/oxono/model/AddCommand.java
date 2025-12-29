package g62496.dev3.oxono.model;

import g62496.dev3.oxono.util.Command;
/**
 * The AddCommand class represents a command to add a piece to the board.
 * It encapsulates the action of placing a piece at a specific position and supports undoing the action.
 */
public class AddCommand implements Command {
    Board board;
    Piece piece;
    int dx;
    int dy;

    /**
     * Constructs an AddCommand with the specified board, piece, and position.
     *
     * @param board the game board where the piece will be placed
     * @param piece the piece to be added
     * @param dx    the x-coordinate where the piece will be placed
     * @param dy    the y-coordinate where the piece will be placed
     */
    public AddCommand(Board board, Piece piece, int dx, int dy) {
        this.board= board;
        this.piece = piece;
        this.dx = dx;
        this.dy= dy;
    }
    /**
     * Executes the command, placing the piece on the board at the specified position.
     */
    @Override
    public void execute() {
        board.setPiece(piece, dx, dy);
    }
    /**
     * Undoes the command, removing the piece from the board at the specified position.
     */
    @Override
    public void unexecute() {
        board.removePieceUndo(dx,dy,piece);
    }
    /**
     * Gets the piece associated with this command.
     *
     * @return the piece to be added to the board
     */

    public Piece getToken() {
        return piece;
    }
}
