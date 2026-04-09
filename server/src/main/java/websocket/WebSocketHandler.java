package websocket;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;

import com.google.gson.Gson;

import chess.ChessMove;
import chess.ChessPosition;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationMessage;



public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand userGameCommand = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (userGameCommand.getCommandType()) {
                case CONNECT -> connect(userGameCommand.getAuthToken(), ctx.session);
                case MAKE_MOVE -> makeMove(userGameCommand.getAuthToken(), new ChessMove(new ChessPosition(1, 1), new ChessPosition(1, 3), null));
                case LEAVE -> leave(userGameCommand.getAuthToken(), ctx.session);
                case RESIGN -> resign(userGameCommand.getAuthToken(), ctx.session);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(String playerName, Session session) throws IOException {
        connections.add(session);
        String message = String.format("%s has joined the game", playerName);
        NotificationMessage serverMessage = new NotificationMessage(message);
        connections.broadcast(session, serverMessage);
    }

    private void leave(String playerName, Session session) throws IOException {
        String message = String.format("%s has left the game", playerName);
        NotificationMessage serverMessage = new NotificationMessage(message);
        connections.broadcast(session, serverMessage);
        connections.remove(session);
    }

    private void resign(String playerName, Session session) throws IOException {
        String message = String.format("%s has resigned from the game", playerName);
        NotificationMessage serverMessage = new NotificationMessage(message);
        connections.broadcast(session, serverMessage);
    }

    public void makeMove(String playerName, ChessMove move) throws IOException {
        String message = String.format("%s moved %s", playerName, move);
        NotificationMessage serverMessage = new NotificationMessage(message);
        connections.broadcast(null, serverMessage);
    }
}
