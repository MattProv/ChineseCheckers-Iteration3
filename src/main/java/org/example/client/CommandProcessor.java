package org.example.client;

import org.example.message.Commands;

public abstract class CommandProcessor {

    public static boolean processInput(String[] input) {
        if (input.length < 1) {
            return false; // No command provided
        }

        // Remove leading slash and get command
        Commands command = Commands.fromString(input[0]);

        if (command == null) {
            return false; // Command not recognized
        }

        // Check if the number of parts matches the expected parts
        if (input.length == command.getExpectedParts())
            return true;
        else {
            System.out.println("Wrong number of arguments: " + (input.length - 1) + ", expected: " + command.getExpectedParts() + "\n");
            return false;
        }
    }
}

