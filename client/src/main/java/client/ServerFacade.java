package client;

import java.util.List;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import model.exceptions.HTTPException;
import model.requests.CreateGameRequest;
import model.requests.JoinGameRequest;
import model.requests.RegisterRequest;
import model.requests.SessionCreationRequest;
import model.results.GameCreationResult;
import model.results.ListGamesResult;
import model.results.RegisterResult;
import model.results.SessionCreationResult;

public class ServerFacade {
    private String authToken;
    private final ClientCommunicator communicator;
    private WebSocketFacade webSocketFacade;

    public String getToken() {
        return this.authToken;
    }

    public ServerFacade(String url) {
        communicator = new ClientCommunicator(url);        
    }

    public String register(String username, String email, String password) throws HTTPException {
        RegisterRequest request = new RegisterRequest(username, password, email);
        RegisterResult response = communicator.post(request, "/user", authToken, RegisterResult.class);
        this.authToken = response.authToken();
        return response.username();
    }

    public String login(String username, String password) throws HTTPException {
        SessionCreationRequest request = new SessionCreationRequest(username, password);
        SessionCreationResult response = communicator.post(request, "/session", authToken, SessionCreationResult.class);
        this.authToken = response.authToken();
        return response.username();
    }

    public void logout() throws HTTPException {
        communicator.delete(null, "/session", authToken);
        this.authToken = null;
    }

    public int createGame(String gameName) throws HTTPException {
        CreateGameRequest request = new CreateGameRequest(authToken, gameName);
        GameCreationResult response = communicator.post(request, "/game", authToken, GameCreationResult.class);
        return response.gameID();
    }

    public List<GameData> listGames() throws HTTPException {
        ListGamesResult response = communicator.get(null, "/game", authToken, ListGamesResult.class);
        return response.games();
    }

    public void joinGame(int gameID, String team, NotificationHandler notificationHandler) throws HTTPException {
        JoinGameRequest request = new JoinGameRequest(authToken, team, gameID);
        communicator.put(request, "/game", authToken, null);
        webSocketFacade = new WebSocketFacade(communicator.getServerUrl(), this::getToken, notificationHandler);
        webSocketFacade.connect(gameID);
    }

    public void leaveGame() throws HTTPException {
        webSocketFacade.leave();
    }

    public void observeGame(int gameID, NotificationHandler notificationHandler) throws HTTPException {
        webSocketFacade = new WebSocketFacade(communicator.getServerUrl(), this::getToken, notificationHandler);
        webSocketFacade.connect(gameID);
    }

    public void resign() throws HTTPException {
        webSocketFacade.resign();
    }

    public void move(ChessPosition from, ChessPosition to, ChessPiece.PieceType promotion) throws HTTPException {
        webSocketFacade.makeMove(new ChessMove(from, to, promotion));
    }

    public void clearDatabase() throws HTTPException {
        communicator.delete(null, "/db", null);
    }
}
