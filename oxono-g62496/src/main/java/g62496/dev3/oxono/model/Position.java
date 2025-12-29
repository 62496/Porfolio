package g62496.dev3.oxono.model;
/**
 * Represents the position of a token on the board using x and y coordinates.
 */
public class Position {
    int dx;
    int dy;
    /**
     * Constructs a Position with specific x and y coordinates.
     *
     * @param dx the x-coordinate
     * @param dy the y-coordinate
     */
    public Position(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }
    /**
     * Returns the x-coordinate of the position.
     *
     * @return the x-coordinate
     */
    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }
    /**
     * Checks if this Position is equal to another Position by comparing both x and y coordinates.
     *
     * @param obj the object to compare
     * @return true if the positions are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // Si c'est la même référence
        if (obj == null || getClass() != obj.getClass()) return false; // Si les classes diffèrent
        Position position = (Position) obj;
        return dx == position.dx && dy == position.dy; // Compare le contenu
    }
}
