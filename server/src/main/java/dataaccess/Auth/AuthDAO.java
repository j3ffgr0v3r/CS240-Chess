package dataaccess.auth;

import dataaccess.DataAccessException;
import model.AuthData;

public interface AuthDAO {

    public void createSession(AuthData authData) throws DataAccessException;

    public AuthData getSession(String authToken) throws DataAccessException;

    public void terminateSession(String authToken) throws DataAccessException;

    public void clear() throws DataAccessException;
}
