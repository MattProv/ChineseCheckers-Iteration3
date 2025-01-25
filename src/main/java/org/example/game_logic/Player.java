package org.example.game_logic;

import org.example.message.PromptMoveMessage;
import org.example.server.Server;
import org.example.server.User;

/**
 * Represents a player in the game, extending the functionality of an agent.
 * A player is associated with a user, has an ID, and can prompt the user to make a move.
 */
public final class Player extends Agent {

    // The user that owns this player, not serialized (transient)
    private transient final User owner;

    /**
     * Constructs a new Player with the specified owner and player ID.
     *
     * @param owner the user who owns the player
     * @param id the unique identifier for the player
     */
    public Player(final User owner, int id) {
        super(id, true);
        this.owner = owner;
    }

    /**
     * Returns the user that owns this player.
     *
     * @return the user who owns the player
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Prompts the owner of this player to make a move.
     * Sends a prompt move message to the user's connection.
     *
     * @param board the current game board
     */
    @Override
    public void promptMove(Board board) {
        Server.getServer().Send(new PromptMoveMessage(), owner.getConnection());
    }
}
