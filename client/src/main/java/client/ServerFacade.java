package client;

import model.exceptions.HTTPException;
import model.requests.CreateGameRequest;
import model.requests.RegisterRequest;
import model.requests.SessionCreationRequest;
import model.results.GameCreationResult;
import model.results.RegisterResult;
import model.results.Result;
import model.results.SessionCreationResult;

public class ServerFacade {
    private String authToken;
    private final ClientCommunicator communicator;

    public ServerFacade(String url) throws ServerCommunicationFailure {
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
}
