package g62496.dev3.oxono.model;
/**
 * Represents a totem on the board. A totem is placed on the board and can be moved during gameplay.
 */
public class Totem implements Token {
    private Color color;
    private  Symbol Type;
    private Position position;
    /**
     * Initializes the totem with a given type and position.
     *
     * @param type the type of the totem (X or O)
     * @param position the position of the totem on the board
     */
    public Totem(Symbol type,Position position) {
        this.Type = type;
        this.position = position;
    }


    @Override
    public void move(Board board, int dx, int dy) {

    }

    @Override
    public void Setcolor(Color color) {;
        this.color = color;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "\u001B[34m" + Type + "\u001B[0m";
    }

    public int getDx() {
        return position.dx;
    }

    public int getDy() {
        return position.dy;
    }

    public Position getPosition() {
        return position;
    }

    public Symbol getType() {
        return Type;
    }
}
