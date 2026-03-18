package client;

import chess.ChessGame;
import chess.ChessPiece;
import ui.ChessClient;

public class ClientMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

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
