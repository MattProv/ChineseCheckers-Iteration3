package org.example;

import org.example.client.Client;
import org.example.client.CommandProcessor;
import org.example.message.CommandMessage;
import org.example.message.Commands;
import org.example.message.StringMessage;
import org.example.message.clientHandlers.GameStateMessageHandler;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ClientMain {

    public static void main(final String[] args) {
        Scanner scanner = new Scanner(System.in);
        Client client = Client.create();

        GameState gameState = new GameState();

        client.AddHandler(new GameStateMessageHandler(gameState));
        boolean isYourTurn = true;


        System.out.println("Welcome to the Client Application!");
        while (true) {
            System.out.println("\nOptions:");
            System.out.println("1. Connect to server");
            System.out.println("2. Exit");
            System.out.print("Enter your choice: ");
            int choice;
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 4.");
                scanner.nextLine();
                continue;
            }
            switch (choice) {
                case 1:
                    if (client.isConnected()) {
                        System.out.println("Already connected to a server!");
                    } else {
                        System.out.print("Enter server host: ");
                        String host = scanner.nextLine();
                        System.out.print("Enter server port: ");
                        int port;
                        try {
                            port = scanner.nextInt();
                            scanner.nextLine();
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid port number. Please try again.");
                            scanner.nextLine();
                            break;
                        }

                        if (client.Connect(host, port)) {
                            System.out.println("Connected to the server successfully.");
                        } else {
                            System.out.println("Failed to connect to the server.");
                        }
                    }
                    break;
                case 2:
                    if (client.isConnected()) {
                        client.Disconnect();
                    }
                    System.out.println("Exiting the application.");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice. Please pick from one of the listed options.");
            }
            while (client.isConnected()) {
                //client.HandleMessages();
                System.out.println("\nMenu:");
                System.out.println("1. Send Message");
                System.out.println("2. Disconnect from Server");
                System.out.println("3. Exit");
                System.out.print("Enter your choice: ");
                try {
                    choice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                } catch (InputMismatchException e) {
                    System.out.println("Invalid choice. Please pick from one of the listed options.");
                    scanner.nextLine();
                    continue;
                }

                switch (choice) {
                    case 1:
                        if (!client.isConnected()) {
                            System.out.println("You are not connected to a server. Please connect first.");
                        } else {
                            System.out.print("Enter your message: ");
                            String input = scanner.nextLine();
                            String[] parts;
                            if (!gameState.isRunning()) {
                                if (!(input.charAt(0) == '/')) {
                                    StringMessage stringMessage = new StringMessage(input);
                                    if (client.send(stringMessage)) {
                                        System.out.println("Message sent successfully.");
                                    } else {
                                        System.out.println("Failed to send the message.");
                                    }
                                } else {
                                    parts = input.substring(1).trim().split("\\s+");
                                    if (CommandProcessor.processInput(parts)) {
                                        CommandMessage commandMessage = new CommandMessage(Commands.fromString(parts[0]), parts);
                                        if (client.send(commandMessage)) {
                                            System.out.println("Command sent successfully.");
                                        } else {
                                            System.out.println("Failed to send the command.");
                                        }
                                    } else {
                                        System.out.println("Invalid input. Please try again.");
                                    }
                                }
                            } else if (!isYourTurn) {
                                System.out.print("It's not your turn right now!");
                                break;
                            } else {
                                parts = input.trim().split("\\s+");
                                while (parts.length != 4) {
                                    System.out.print("Incorrect input. Provide start and end square of your move.");
                                    input = scanner.nextLine();
                                    parts = input.trim().split("\\s+");
                                }
                                // TODO : Check if the move is valid, get the start and end coordinates
//
//                                MoveMessage moveMessage = new MoveMessage(parts[0], parts[1], parts[2], parts[3]);
//                                if (client.send(moveMessage)) {
//                                    System.out.println("Move sent successfully.");
//                                } else {
//                                    System.out.println("Failed to send the move.");
//                                }
                            }
                        }
                        break;

                    case 2:
                        if (client.isConnected()) {
                            client.Disconnect();
                            System.out.println("Disconnected from the server.");
                        } else {
                            System.out.println("You are not connected to any server.");
                        }
                        break;

                    case 3:
                        if (client.isConnected()) {
                            client.Disconnect();
                        }
                        System.out.println("Exiting the application.");
                        scanner.close();
                        return;

                    default:
                        System.out.println("Invalid choice. Please pick from one of the listed options.");
                }
            }
        }
    }
}
