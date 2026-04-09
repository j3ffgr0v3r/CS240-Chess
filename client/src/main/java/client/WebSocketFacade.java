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
import websocket.messages.NotificationMessage;

// Need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;

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
            this.session.addMessageHandler((MessageHandler.Whole<String>) (String message) -> {
                NotificationMessage notification = new Gson().fromJson(message, NotificationMessage.class);
                notificationHandler.notify(notification);
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
    }

    public void leave(int gameID) throws HTTPException {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken.get(), gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new HTTPException(500, ex.getMessage());
        }
    }

    public void resign(int gameID) throws HTTPException {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken.get(), gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new HTTPException(500, ex.getMessage());
        }
    }

    public void makeMove(int gameID, ChessMove move) throws HTTPException {
        try {
            var action = new MakeMoveCommand(move, authToken.get(), gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new HTTPException(500, ex.getMessage());
        }
    }

}
