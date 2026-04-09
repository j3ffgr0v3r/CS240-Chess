package client;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import ui.ChessClient;

public class ClientMain {
    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.out.println("♔  240 Chess Client ♚ \n");

        String serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }

        try {
            new ChessClient(serverUrl).run();

        } catch (ServerCommunicationFailure ex) {
            System.out.printf("Unable to communicate with server: %s%n", ex.getMessage());
        }
    }
}
