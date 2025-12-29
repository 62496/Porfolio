package g62496.dev3.oxono.model;

import g62496.dev3.oxono.util.BotStrategy;

import java.util.List;
import java.util.Random;

/**
 * The BoteasyStrategy class implements a basic bot strategy for the game.
 * It chooses a random totem and token placement for the bot's moves.
 */
public class BoteasyStrategy implements BotStrategy {

    Board board;
    Facade facade;
    /**
     * Initializes the BoteasyStrategy with the specified board and facade.
     *
     * @param board the board to interact with for the bot's moves
     * @param facade the facade to manage the game state
     */
    public BoteasyStrategy(Board board, Facade facade ) {
        this.board = board;
        this.facade = facade;
    }
    /**
     * Executes the bot's move by selecting a random totem and token placement.
     * The bot moves a totem to a valid position and then places a token adjacent to it.
     *
     * @param oxono the current game state
     */
    @Override
    public void play(Oxono oxono) {
        //Symbol currentSymbol = oxono.getCurrentTotem().getType();
        CurrentPlayer currentPlayer = oxono.getCurrentPlayer();

        Random random = new Random();
        List<Totem> totems = board.getTotems();
        int randomTotemIndex = random.nextInt(totems.size());

        List<Position> rightPosList= board.checkTotemBox(board.getTotems().get(randomTotemIndex).getPosition());

        int randomIndex = random.nextInt(rightPosList.size());
        Symbol currentSymbol = board.getTotems().get(randomTotemIndex).getType();

        Position newPosition = rightPosList.get(randomIndex);
        facade.moveTotem(new Totem(currentSymbol, newPosition), board.getTotems().get(randomTotemIndex).getPosition(),board);


        List<Position> rightPosList2 = board.checkPieceBox(newPosition);
        if (rightPosList2.isEmpty()) {
            return;
        }

        Random random2 = new Random();
        int randomIndex2 = random2.nextInt(rightPosList2.size());

        facade.insertToken(new Piece(currentPlayer.getColor(), currentSymbol), rightPosList2.get(randomIndex2),board);

    }
}
