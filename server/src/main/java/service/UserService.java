package service;

import java.util.UUID;

import dataaccess.DataAccessException;
import dataaccess.auth.AuthDAO;
import dataaccess.user.UserDAO;
import model.AuthData;
import model.UserData;
import requestsandresults.register.RegisterRequest;
import requestsandresults.register.RegisterResult;
import requestsandresults.sessioncreation.SessionCreationRequest;
import requestsandresults.sessioncreation.SessionCreationResult;

public class UserService extends Service {

    UserDAO userDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        super(authDAO);
        this.userDAO = userDAO;
    }

    public RegisterResult register(RegisterRequest request) throws BadRequestException, AlreadyTakenException, DataAccessException {

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

    public SessionCreationResult login(SessionCreationRequest request) throws BadRequestException, UnauthorizedException, DataAccessException {

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

    public void logout(String authToken) throws BadRequestException, UnauthorizedException {
        if (authToken == null) {
            throw new BadRequestException();
        }

        isAuthorized(authToken);

        authDAO.terminateSession(authToken);
    }

    public void clear() {
        userDAO.clear();
        authDAO.clear();
    }

    private String createSession(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();

        authDAO.createSession(new AuthData(authToken, username));

        return authToken;
    }
}
