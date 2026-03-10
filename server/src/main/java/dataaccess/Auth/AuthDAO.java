package dataaccess.auth;

import dataaccess.DataAccessException;
import model.AuthData;

public interface AuthDAO {

    public void createSession(AuthData authData) throws DataAccessException;

    public AuthData getSession(String authData);

    public void terminateSession(String authData);

    public void clear();
}
