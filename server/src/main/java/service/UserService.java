package service;

import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;

import dataaccess.DataAccessException;
import dataaccess.auth.AuthDAO;
import dataaccess.user.UserDAO;
import model.AuthData;
import model.UserData;
import model.exceptions.AlreadyTakenException;
import model.exceptions.BadRequestException;
import model.exceptions.UnauthorizedException;
import model.requests.RegisterRequest;
import model.requests.SessionCreationRequest;
import model.results.RegisterResult;
import model.results.SessionCreationResult;

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

        userDAO.createUser(new UserData(username, BCrypt.hashpw(request.password(), BCrypt.gensalt()), request.email()));

        return new RegisterResult(username, createSession(username));
    }

    public SessionCreationResult login(SessionCreationRequest request) throws BadRequestException, UnauthorizedException, DataAccessException {

        if (request == null || request.username() == null || request.password() == null) {
            throw new BadRequestException();
        }

        String username = request.username();
        UserData user = userDAO.getUser(username);

        if (user == null || !BCrypt.checkpw(request.password(), user.password())) {
            throw new UnauthorizedException();
        }

        return new SessionCreationResult(username, createSession(username));
    }

    public void logout(String authToken) throws BadRequestException, UnauthorizedException, DataAccessException {
        if (authToken == null) {
            throw new BadRequestException();
        }

        isAuthorized(authToken);

        authDAO.terminateSession(authToken);
    }

    public void clear() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
    }

    public String getUserFromAuth(String authToken) throws UnauthorizedException, DataAccessException {
        AuthData authData = authDAO.getSession(authToken);
        if (authData == null) {
            throw new UnauthorizedException();
        }
        return authData.username();
    }

    private String createSession(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();

        authDAO.createSession(new AuthData(authToken, username));

        return authToken;
    }
}
