package client;

import model.exceptions.AlreadyTakenException;
import model.exceptions.BadRequestException;
import model.exceptions.HTTPException;
import model.requests.RegisterRequest;
import model.results.RegisterResult;
import model.results.Result;

public class ServerFacade {
    private String authToken;
    private final ClientCommunicator communicator;

    public ServerFacade(String url) throws ServerCommunicationFailure {
        communicator = new ClientCommunicator(url);
    }

    public String register(String username, String email, String password) throws BadRequestException, AlreadyTakenException {
        RegisterRequest request = new RegisterRequest(username, password, email);
        try {
            Result response = communicator.post(request, "/user");
            if (response instanceof RegisterResult res) {
                this.authToken = res.authToken();
                return res.username();
            }
            throw new BadRequestException();
        } catch (HTTPException e) {
            throw new BadRequestException();
        }
        
    }
}
