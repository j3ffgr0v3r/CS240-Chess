package service;

import java.util.UUID;

import dataaccess.Auth.AuthDAO;
import dataaccess.User.UserDAO;
import model.AuthData;
import model.UserData;
import requestsandresults.Register.RegisterRequest;
import requestsandresults.Register.RegisterResult;
import requestsandresults.SessionCreation.SessionCreationRequest;
import requestsandresults.SessionCreation.SessionCreationResult;

public class UserService {

    UserDAO userDAO;
    AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) {
        String username = registerRequest.username();
        UserData user = userDAO.getUser(username);

        if (user != null) {
            return new RegisterResult("Error: Username already taken.", null, null);
        }

        userDAO.createUser(new UserData(username, registerRequest.password(), registerRequest.email()));

        return new RegisterResult(null, username, createSession(username));
    }

    public SessionCreationResult login(SessionCreationRequest sessionCreationRequest) {
        String username = sessionCreationRequest.username();

        UserData user = userDAO.getUser(username);

        if (user == null || (user.password() == null ? sessionCreationRequest.password() != null : !user.password().equals(sessionCreationRequest.password()))) {
            return new SessionCreationResult("Error: unauthorized", null, null);
        }

        return new SessionCreationResult(null, username, createSession(username));
    }

    public String logout(String authToken) {
        AuthData authData = authDAO.getSession(authToken);

        if (authData == null) {
            return "Error: unauthorized";
        }

        authDAO.terminateSession(authToken);

        return null; 
    }

    public void clear() {
        userDAO.clear();
        authDAO.clear();
    }

    private String createSession(String username) {
        String authToken = UUID.randomUUID().toString();

        authDAO.createSession(new AuthData(authToken, username));

        return authToken;
    }
}
