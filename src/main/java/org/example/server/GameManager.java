package org.example.server;

import org.example.GameState;
import org.example.game_logic.*;
import org.example.message.GameStateMessage;
import org.example.message.UserlistMessage;
import org.example.server.db.GameDocument;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the game state, users, game flow, and synchronization in the game.
 * This class follows the Singleton design pattern and ensures that only one instance
 * of the game manager exists.
 */
public final class GameManager {

    private static GameManager instance = new GameManager();

    // Lobby to hold users waiting to play
    private final List<User> lobby = new ArrayList<>();

    // Game settings
    private int playerCount = 2;

    // Runtime game state and agents
    private final GameState gameState = new GameState();
    private List<Agent> agents = new ArrayList<>();
    private int currentTurn = 0;
    private int botsCount = 0;
    private int playersFinished = 0;

    // Ruleset for the game
    private Rules ruleset = new StandardRules();
    public GameManagerCallbackHandler gameManagerCallbackHandler = new GameManagerCallbackHandler();

    // Game saving and loading
    private String gameName;
    private GameDocument gameToBeLoaded;

    /**
     * Private constructor for Singleton design pattern.
     */
    private GameManager() {
    }

    /**
     * Sets the ruleset for the game. The rules cannot be changed if the game is already running.
     *
     * @param ruleset the ruleset to be used
     */
    public void setRuleset(Rules ruleset) {
        if (gameState.isRunning()) {
            gameManagerCallbackHandler.onRulesNotChanged("Game already running!");
            return;
        }
        gameToBeLoaded = null;
        this.ruleset = ruleset;
        gameManagerCallbackHandler.onRulesChanged("Rules changed!");
    }

    /**
     * Creates and returns the singleton instance of GameManager.
     *
     * @return the singleton instance of GameManager
     */
    public static GameManager create() {
        return instance = new GameManager();
    }

    /**
     * Returns the singleton instance of GameManager.
     *
     * @return the singleton instance of GameManager
     */
    public static GameManager getInstance() {
        return instance;
    }

    /**
     * Starts the game if conditions are met (valid number of users, game board, and ruleset).
     * Initializes the agents and sets up the board, then starts the game.
     *
     * @param users list of users who are playing the game
     * @return true if the game started successfully, false otherwise
     */
    public boolean startGame(final List<ServerConnection> users) {
        if (gameState.isRunning()) {
            gameManagerCallbackHandler.onGameNotStarted("Game already running!");
            return false;
        }

        if (users.size() + botsCount != playerCount) {
            String msg = "Game cannot be started, " + users.size() + " users connected out of " + playerCount + " required!";
            gameManagerCallbackHandler.onGameNotStarted(msg);
            return false;
        }

        if (gameState.getBoard() == null) {
            gameManagerCallbackHandler.onGameNotStarted("No board set!");
            return false;
        }

        if (ruleset == null) {
            gameManagerCallbackHandler.onGameNotStarted("No ruleset set!");
            return false;
        }

        currentTurn = 0;
        agents.clear();
        for (User user : lobby) {
            agents.add(new Player(user, agents.size()));
        }
        for (int i = 0; i < botsCount; i++) {
            agents.add(new Bot(agents.size()));
        }
        gameState.getBoard().generateBoard();
        gameState.getBoard().defineBases();
        gameState.getBoard().defineNeighbours();
        ruleset.assignBasesToAgents(gameState.getBoard(), agents);
        ruleset.setupBoard(gameState.getBoard(), agents);
        gameState.setRunning(true);

        if(gameToBeLoaded != null) {
            gameToBeLoaded.getMoves().forEach(move -> {
                Move m = new Move(gameState.getBoard().getNode(move.getStartPosition()), gameState.getBoard().getNode(move.getEndPosition()));
                gameState.getBoard().move(m);
            });

            currentTurn = gameToBeLoaded.getCurrentTurn();
            gameToBeLoaded = null;
        }

        agents.get(currentTurn).promptMove(gameState.getBoard());

        synchronizeGameState();

        gameManagerCallbackHandler.onGameStarted();
        return true;
    }

    /**
     * Sets the board for the game. Cannot change if the game is already running.
     *
     * @param board the board to set
     * @return true if the board was successfully set, false otherwise
     */
    public boolean setBoard(final Board board) {
        if (gameState.isRunning()) {
            gameManagerCallbackHandler.onBoardNotChanged("Game already running!");
            return false;
        }

        gameToBeLoaded = null;
        gameState.setBoard(board);
        gameManagerCallbackHandler.onBoardChanged("Board set!");

        return true;
    }

    /**
     * Sets the number of players required for the game. Validates player count before setting.
     *
     * @param playerCount the desired number of players
     * @return true if the player count was successfully set, false otherwise
     */
    public boolean setPlayerCount(final int playerCount) {
        if (gameState.isRunning()) {
            gameManagerCallbackHandler.onPlayerCountNotChanged(playerCount, "Game already running!");
            return false;
        }
        if (!ruleset.validatePlayerCount(playerCount)) {
            gameManagerCallbackHandler.onPlayerCountNotChanged(playerCount, "Invalid player count!");
            return false;
        }

        gameToBeLoaded = null;
        gameManagerCallbackHandler.onPlayerCountChanged(this.playerCount, playerCount);
        this.playerCount = playerCount;
        return true;
    }

    /**
     * Processes a move made by an agent. Validates and applies the move if possible.
     *
     * @param agent the agent making the move
     * @param move the move being made
     * @return true if the move was valid and applied, false otherwise
     */
    public boolean makeMove(final Agent agent, final Move move) {
        if (!gameState.isRunning()) {
            gameManagerCallbackHandler.onInvalidMove(agent, move, "Game not running!");
            return false;
        }

        if (agent != agents.get(currentTurn)) {
            gameManagerCallbackHandler.onInvalidMove(agent, move, "Not your turn!");
            return false;
        }

        if (gameState.getBoard().getPawn(move.getStart()) == null) {
            gameManagerCallbackHandler.onInvalidMove(agent, move, "There's no pawn there to move!");
            return false;
        }

        if (gameState.getBoard().getPawn(move.getStart()).getOwner() != agent) {
            gameManagerCallbackHandler.onInvalidMove(agent, move, "You can't move another player's pawns!");
            return false;
        }
        if (ruleset.validateMove(gameState.getBoard(), move) || !agent.isPlayer()) {
            gameState.getBoard().move(move);
            synchronizeGameState();
            gameManagerCallbackHandler.onValidMove(agent, move, "Valid move!");
            return true;
        } else {
            System.out.println("Validation failed!");
        }
        gameManagerCallbackHandler.onInvalidMove(agent, move, "Invalid move!");
        return false;
    }

    /**
     * Retrieves the player associated with a given connection.
     *
     * @param sc the server connection to look up
     * @return the player associated with the connection, or null if not found
     */
    public Player getPlayerByConnection(ServerConnection sc) {
        for (Agent agent : agents) {
            if (agent.isPlayer()) {
                Player player = (Player) agent;
                if (player.getOwner().getConnection() == sc)
                    return player;
            }
        }
        return null;
    }

    /**
     * Retrieves the user associated with a given connection.
     *
     * @param sc the server connection to look up
     * @return the user associated with the connection, or null if not found
     */
    public User getUserByConnection(ServerConnection sc) {
        for (User user : lobby) {
            if (user.getConnection() == sc)
                return user;
        }
        return null;
    }

    /**
     * Synchronizes the game state across all players.
     * This method sends a game state update to all connected players.
     */
    public void synchronizeGameState() {
        System.out.println("Synchronizing game state.");
        List<String> playerNames = new ArrayList<>();
        for (Agent agent : agents) {
            playerNames.add(agent.isPlayer() ? ((Player) agent).getOwner().getUsername() : "AI-"+(((Bot)agent).getId().toString()));
        }
        String[] playerNamesArray = new String[playerNames.size()];
        playerNamesArray = playerNames.toArray(playerNamesArray);

        GameStateMessage gsm = new GameStateMessage(gameState.clone(), playerNamesArray, currentTurn);
        gsm.getGameState().getBoard().showBoard();
        Server.getServer().Broadcast(gsm);
    }

    /**
     * Synchronizes the user list across all players.
     * This method sends the user list to all connected players.
     */
    public void synchronizeUsers() {
        System.out.println("Synchronizing users.");
        List<String> usernames = new ArrayList<>();
        for (User user : lobby) {
            usernames.add(user.getUsername());
        }
        String[] usernamesArray = new String[usernames.size()];
        usernamesArray = usernames.toArray(usernamesArray);
        Server.getServer().Broadcast(new UserlistMessage(usernamesArray));
    }

    /**
     * Adds a user to the lobby.
     *
     * @param user the user to add to the lobby
     */
    public void addUser(final User user) {
        lobby.add(user);
    }

    /**
     * Removes a user from the lobby by their connection.
     *
     * @param sc the server connection of the user to remove
     */
    public void removeUser(ServerConnection sc) {
        User user = getUserByConnection(sc);
        if (user != null)
            lobby.remove(user);
    }

    /**
     * Makes a move from coordinates (start to end) for an agent.
     *
     * @param agent the agent making the move
     * @param start the starting coordinate
     * @param end the ending coordinate
     * @return true if the move was successfully made, false otherwise
     */
    public boolean makeMoveFromCoordinates(final Agent agent, Coordinate start, Coordinate end) {
        Move move = new Move(gameState.getBoard().getNode(start), gameState.getBoard().getNode(end));
        return makeMove(agent, move);
    }

    /**
     * Ends the current turn for an agent, checks win conditions, and moves to the next turn if necessary.
     *
     * @param agent the agent whose turn is being ended
     */
    public void endTurn(Agent agent) {
        if (agent != agents.get(currentTurn))
            return;
        agent.liftLocks();

        if (ruleset.checkWinCondition(agents.get(currentTurn))) {
            agents.get(currentTurn).setHasWon(true);
            playersFinished++;
            gameManagerCallbackHandler.onPlayerFinished(agent, playersFinished);
            if (playersFinished+1 >= agents.size()) {
                gameManagerCallbackHandler.onGameEnded();
                return;
            }
        }
        Agent oldTurn = agents.get(currentTurn);
        do {
            currentTurn = (currentTurn + 1) % agents.size();
        }
        while (agents.get(currentTurn).getHasWon());
        synchronizeGameState();

        gameManagerCallbackHandler.onTurnChange(oldTurn, agents.get(currentTurn), currentTurn);

        agents.get(currentTurn).promptMove(gameState.getBoard());
    }

    public BoardType getBoardType() {
        return gameState.getBoard().getBoardType();
    }

    public RulesType getRulesType() {
        return ruleset.getRulesType();
    }

    public int getPlayersCount() {
        return playerCount;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
        gameManagerCallbackHandler.onGameNameChanged(gameName);
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameToBeLoaded(GameDocument gameToBeLoaded) {
        if(gameState.isRunning()) {
            gameManagerCallbackHandler.onGameNotLoaded("Game already running!");
            return;
        }

        playerCount = gameToBeLoaded.getPlayersCount();
        ruleset = gameToBeLoaded.getRulesType().createRules();
        gameState.setBoard(gameToBeLoaded.getBoardType().createBoard());

        gameManagerCallbackHandler.onGameLoaded(gameToBeLoaded.getName());

        this.gameToBeLoaded = gameToBeLoaded;
    }

    public GameDocument getGameToBeLoaded() {
        return gameToBeLoaded;
    }

    public void setBotsCount(int botsCount) {
        this.botsCount = botsCount;
        gameManagerCallbackHandler.onBotsCountChanged(botsCount);
    }
}
