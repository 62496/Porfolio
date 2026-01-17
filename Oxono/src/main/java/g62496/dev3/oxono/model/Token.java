package g62496.dev3.oxono.model;
/**
 * Interface that represents a token (either Piece or Totem) on the game board.
 * A token can be moved and have a color.
 */
public interface Token {

    /**
     * Moves the token to a new position on the board.
     *
     * @param board the board on which the token resides
     * @param dx the new x-coordinate for the token
     * @param dy the new y-coordinate for the token
     */
    void move(Board board, int dx, int dy);
    /**
     * Sets the color of the token.
     *
     * @param color the color to set
     */
    void Setcolor(Color color);

    /**
     * Returns the color of the token.
     *
     * @return the color of the token
     */
    Color getColor();

}
