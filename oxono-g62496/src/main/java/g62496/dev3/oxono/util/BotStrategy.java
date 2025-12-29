package g62496.dev3.oxono.util;
import g62496.dev3.oxono.model.Oxono;

/**
 * The BotStrategy interface defines the strategy that a bot will use to make moves in the Oxono game.
 * It provides a method to execute the bot's move based on the current game state.
 */
public interface BotStrategy {
    /**
     * Executes the bot's move in the Oxono game.
     *
     * @param oxono the current game state
     */
    void play(Oxono oxono);
}

