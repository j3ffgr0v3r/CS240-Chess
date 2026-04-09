package client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Supplier;

import com.google.gson.Gson;

import chess.ChessMove;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import model.exceptions.HTTPException;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

// Need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;
    int gameID;

    private final Supplier<String> authToken;

    public WebSocketFacade(String url, Supplier<String> tokenSupplier, NotificationHandler notificationHandler) throws HTTPException {
        this.authToken = tokenSupplier;
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            // Set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    // System.out.println("Received: " + message);
                    notificationHandler.notify(message);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new HTTPException(500, ex.getMessage());
        }
    }

    // Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(int gameID) throws HTTPException {
        try {
            UserGameCommand action = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken.get(), gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new HTTPException(500, ex.getMessage());
        }
        this.gameID = gameID;
    }

    public void leave() throws HTTPException {
        try {
            UserGameCommand action = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken.get(), gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new HTTPException(500, ex.getMessage());
        }
        this.gameID = 0;
    }

    public void resign() throws HTTPException {
        try {
            UserGameCommand action = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken.get(), gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new HTTPException(500, ex.getMessage());
        }
    }

    public void makeMove(ChessMove move) throws HTTPException {
        try {
            MakeMoveCommand action = new MakeMoveCommand(move, authToken.get(), gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new HTTPException(500, ex.getMessage());
        }
    }

}
