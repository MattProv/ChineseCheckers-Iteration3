package org.example.message;

public enum Commands {
    SET_PLAYER_COUNT(2),
    START_GAME(1);

    private final int expectedParts;

    Commands(final int expectedParts) {
        this.expectedParts = expectedParts;
    }

    public int getExpectedParts() {
        return expectedParts;
    }

    // Method to fetch the enum constant by name (ignoring case)
    public static Commands fromString(final String command) {
        for (Commands c : Commands.values()) {
            if (c.name().equalsIgnoreCase(command)) {
                return c;
            }
        }
        return null; // Command not found
    }
}
