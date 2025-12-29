package g62496.dev3.oxono.model;
/**
 * Represents the current player in the game.
 * The player alternates between two colors: PINK and BLACK.
 */
public class CurrentPlayer {

    private Color color ;
    /**
     * Initializes the current player to PINK.
     */
    public CurrentPlayer( ) {
        this.color = Color.PINK;
    }
    /**
     * Gets the color of the current player.
     *
     * @return the current player's color
     */
    public Color getColor() {
        return color;
    }
    /**
     * Switches the turn to the next player.
     */
    public void next(){
        if(color==Color.PINK){
            color= Color.BLACK;
        }else{
            color = Color.PINK;
        }
    }

}
