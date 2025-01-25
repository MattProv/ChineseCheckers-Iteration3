package org.example.game_logic;

public enum BoardType {
    STANDARD{
        @Override
        public Board createBoard() {
            return new StandardBoard();
        }

        @Override
        public String toString() {
            return "Standard";
        }
    };

    public abstract Board createBoard();
}
