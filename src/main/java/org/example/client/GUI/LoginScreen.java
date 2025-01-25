package org.example.client.GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.example.Config;

import java.util.Objects;

public class LoginScreen extends BorderPane {

    public abstract static class CallbacksHandler {
        public void onConnect(String username, String ip, int port)
        {
            System.out.println("Username: " + username);
            System.out.println("IP: " + ip);
            System.out.println("Port: " + port);
        }

        public boolean validateUsername(String username)
        {
            return !username.isEmpty();
        }

        public boolean validateIP(String ip)
        {
            return !ip.isEmpty();
        }

        public void onError(String message)
        {
            System.out.println("Error: " + message);
        }
    }

    private CallbacksHandler callbacksHandler;

    public LoginScreen() {
        super();

        GridPane gridPane = new GridPane();

        Label usernameLabel = new Label("Username");
        TextField usernameTextField = new TextField();
        usernameTextField.setPromptText("Enter your username");

        Label ipLabel = new Label("Server IP");
        TextField ipTextField = new TextField();
        ipTextField.setPromptText("Enter server IP");

        Label portLabel = new Label("Server Port");
        TextField portTextField = new TextField();
        portTextField.setPromptText("Enter server port");
        portTextField.setText(Integer.toString(Config.PORT));

        Button connectButton = new Button("Connect");
        connectButton.setOnAction(ActionEvent -> {
            String username = usernameTextField.getText();
            String ip = ipTextField.getText();
            String portString = portTextField.getText();

            int port;

            try
            {
                port = Integer.parseInt(portString);
                if (port < 0 || port > 65535)
                {
                    callbacksHandler.onError("Invalid port number. Please enter a number between 0 and 65535.");
                    return;
                }
            }
            catch (NumberFormatException ex)
            {
                callbacksHandler.onError("Invalid port number. Please enter a number between 0 and 65535.");
                return;
            }

            if(!callbacksHandler.validateIP(ip))
            {
                callbacksHandler.onError("Invalid IP. Please enter a valid IP.");
                return;
            }

            if(!callbacksHandler.validateUsername(username))
            {
                callbacksHandler.onError("Invalid username. Please enter a valid username.");
                return;
            }

            callbacksHandler.onConnect(username, ip, port);
        });

        gridPane.setMinSize(300, 300);
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(usernameLabel, 0, 0);
        gridPane.add(usernameTextField, 1, 0);
        gridPane.add(ipLabel, 0, 1);
        gridPane.add(ipTextField, 1, 1);
        gridPane.add(portLabel, 0, 2);
        gridPane.add(portTextField, 1, 2);

        VBox vbox = new VBox();
        vbox.getChildren().addAll(gridPane, connectButton);
        vbox.setAlignment(Pos.TOP_CENTER);

        getStylesheets().add(Objects.requireNonNull(getClass().getResource("/loginstyles.css")).toExternalForm());

        this.setCenter(vbox);

    }

    public void setCallbacksHandler(CallbacksHandler callbacksHandler)
    {
        this.callbacksHandler = callbacksHandler;
    }
}
