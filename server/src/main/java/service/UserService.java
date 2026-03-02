package service;

import java.util.UUID;

import dataaccess.Auth.AuthDAO;
import dataaccess.User.UserDAO;
import model.AuthData;
import model.UserData;
import requestsandresults.RegisterRequest;
import requestsandresults.RegisterResult;
import requestsandresults.SessionCreationRequest;
import requestsandresults.SessionCreationResult;

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
            return new SessionCreationResult("Error: Invalid Credentials.", null, null);
        }

        return new SessionCreationResult(null, username, createSession(username));
    }

    public void clear() {
        userDAO.clear();
        authDAO.clear();
    }

    private String createSession(String username) {
        String authToken = UUID.randomUUID().toString();

        authDAO.createAuth(new AuthData(authToken, username));

        return authToken;
    }
}
