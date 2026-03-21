package ui;


import client.ServerCommunicationFailure;
import model.exceptions.AlreadyTakenException;
import model.exceptions.BadRequestException;

public class ServerFacade {
    private String authToken;

    public ServerFacade(String url) throws ServerCommunicationFailure {
        
    }

    public String register(String username, String email, String password) throws BadRequestException, AlreadyTakenException {
        return "username";
    }
}
