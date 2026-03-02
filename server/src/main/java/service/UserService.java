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

    public RegisterResult register(RegisterRequest request) throws BadRequestException, AlreadyTakenException {

        if (request == null || request.username() == null || request.password() == null || request.email() == null) {
            throw new BadRequestException();
        }

        String username = request.username();
        UserData user = userDAO.getUser(username);

        if (user != null) {
            throw new AlreadyTakenException();
        }

        userDAO.createUser(new UserData(username, request.password(), request.email()));

        return new RegisterResult(username, createSession(username));
    }

    public SessionCreationResult login(SessionCreationRequest request) throws BadRequestException, UnauthorizedException {

        if (request == null || request.username() == null || request.password() == null) {
            throw new BadRequestException();
        }

        String username = request.username();
        UserData user = userDAO.getUser(username);

        if (user == null || (user.password() == null ? request.password() != null : !user.password().equals(request.password()))) {
            throw new UnauthorizedException();
        }

        return new SessionCreationResult(username, createSession(username));
    }

    public String logout(String authToken) throws BadRequestException, UnauthorizedException {
        if (authToken == null) {
            throw new BadRequestException();
        }

        isAuthorized(authToken);

        authDAO.terminateSession(authToken);

        return null; 
    }

    public void clear() {
        userDAO.clear();
        authDAO.clear();
    }

    
    public void isAuthorized(String authToken) throws UnauthorizedException {
        if (authDAO.getSession(authToken) == null) {
            throw new UnauthorizedException();
        }
    }

    private String createSession(String username) {
        String authToken = UUID.randomUUID().toString();

        authDAO.createSession(new AuthData(authToken, username));

        return authToken;
    }
}
