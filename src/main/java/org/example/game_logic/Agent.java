package org.example.game_logic;

import org.example.SerializableColor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an agent in the game, which can control pawns and participate as a player or an AI.
 * Each agent has a unique ID, color, and a list of pawns they control.
 * The agent also tracks its start and finish bases, locks for moves, and the current pawn being controlled.
 */
public class Agent implements Serializable {

    /**
     * Unique identifier for the agent.
     */
    private int id;

    /**
     * List of pawns controlled by the agent.
     */
    private List<Pawn> pawns = new ArrayList<>();

    /**
     * The color representing the agent.
     */
    private SerializableColor color;

    /**
     * Index of the base where the agent starts.
     */
    private int startBaseIndex;

    /**
     * Index of the base where the agent finishes.
     */
    private int finishBaseIndex;

    /**
     * Indicates if the agent's step action is currently locked.
     */
    private boolean stepLocked = false;

    /**
     * Indicates if the agent's hop action is currently locked.
     */
    private boolean hopLocked = false;

    /**
     * The pawn currently being controlled by the agent.
     */
    private Pawn currentPawn = null;

    /**
     * Indicates if the agent represents a player (true) or an AI (false).
     */
    private boolean isPlayer = false;

    /**
     * Creates an agent with a unique ID and specifies if it is a player or an AI.
     * Assigns the agent a random color.
     *
     * @param id       the unique identifier for the agent.
     * @param isPlayer true if the agent is a player, false if it is an AI.
     */
    public Agent(int id, boolean isPlayer) {
        this.id = id;
        this.isPlayer = isPlayer;
        this.color = new SerializableColor(
                (int) (Math.random() * 256),
                (int) (Math.random() * 256),
                (int) (Math.random() * 256)
        );
    }

    /**
     * Creates an agent with a unique ID and assigns start and finish bases.
     *
     * @param id              the unique identifier for the agent.
     * @param startBaseIndex  the index of the base where the agent starts.
     * @param finishBaseIndex the index of the base where the agent finishes.
     */
    public Agent(int id, int startBaseIndex, int finishBaseIndex) {
        this.id = id;
        this.startBaseIndex = startBaseIndex;
        this.finishBaseIndex = finishBaseIndex;
    }

    /**
     * Retrieves the list of pawns controlled by the agent.
     *
     * @return the list of pawns.
     */
    public List<Pawn> getPawns() {
        return pawns;
    }

    /**
     * Adds a pawn to the list of pawns controlled by the agent.
     *
     * @param pawn the {@link Pawn} to add.
     */
    public void addPawn(Pawn pawn) {
        pawns.add(pawn);
    }

    /**
     * Assigns the start and finish bases to the agent.
     *
     * @param startBaseIndex  the index of the start base.
     * @param finishBaseIndex the index of the finish base.
     */
    void assignBases(int startBaseIndex, int finishBaseIndex) {
        this.startBaseIndex = startBaseIndex;
        this.finishBaseIndex = finishBaseIndex;
    }

    /**
     * Retrieves the index of the agent's start base.
     *
     * @return the start base index.
     */
    int getStartBaseIndex() {
        return this.startBaseIndex;
    }

    /**
     * Retrieves the index of the agent's finish base.
     *
     * @return the finish base index.
     */
    public int getFinishBaseIndex() {
        return this.finishBaseIndex;
    }

    /**
     * Checks if the agent is a player.
     *
     * @return true if the agent is a player, false otherwise.
     */
    public boolean isPlayer() {
        return isPlayer;
    }

    /**
     * Retrieves the unique identifier of the agent.
     *
     * @return the agent ID.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Checks if the agent's step action is locked.
     *
     * @return true if step is locked, false otherwise.
     */
    public boolean isStepLocked() {
        return stepLocked;
    }

    /**
     * Locks the agent's step action.
     */
    public void stepLock() {
        this.stepLocked = true;
    }

    /**
     * Checks if the agent's hop action is locked.
     *
     * @return true if hop is locked, false otherwise.
     */
    public boolean isHopLocked() {
        return hopLocked;
    }

    /**
     * Locks the agent's hop action.
     */
    public void hopLock() {
        this.hopLocked = true;
    }

    /**
     * Sets the current pawn being controlled by the agent.
     *
     * @param currentPawn the {@link Pawn} to set as the current pawn.
     */
    public void setCurrentPawn(Pawn currentPawn) {
        this.currentPawn = currentPawn;
    }

    /**
     * Retrieves the current pawn being controlled by the agent.
     *
     * @return the current {@link Pawn}.
     */
    public Pawn getCurrentPawn() {
        return currentPawn;
    }

    /**
     * Unlocks the step and hop actions and clears the current pawn.
     */
    public void liftLocks() {
        this.stepLocked = false;
        this.hopLocked = false;
        this.currentPawn = null;
    }

    /**
     * Prompts the agent to perform a move on the given board.
     * This method can be overridden for custom behavior.
     *
     * @param board the {@link Board} on which the agent performs a move.
     */
    public void promptMove(Board board) {
        // To be implemented based on specific game logic
    }

    /**
     * Sets the color of the agent.
     *
     * @param color the {@link SerializableColor} to set.
     */
    public void setColor(SerializableColor color) {
        this.color = color;
    }

    /**
     * Retrieves the color representing the agent.
     *
     * @return the {@link SerializableColor} of the agent.
     */
    public SerializableColor getColor() {
        return color;
    }

    public void setAgentList(List<Agent> agents) {
        return;
    }
}
