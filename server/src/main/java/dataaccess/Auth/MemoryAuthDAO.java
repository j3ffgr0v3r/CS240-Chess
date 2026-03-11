package dataaccess.auth;

import java.util.HashMap;
import java.util.Map;

import dataaccess.DataAccessException;
import model.AuthData;

public class MemoryAuthDAO implements AuthDAO {

    Map<String, AuthData> sessions;

    public MemoryAuthDAO() {
        sessions = new HashMap<>();
    }

    @Override
    public void createSession(AuthData authData) throws DataAccessException {
        if (sessions.get(authData.authToken()) != null) {
            throw new DataAccessException("Error: Duplicate entry attempt");
        }
        sessions.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getSession(String authToken) {
        return sessions.get(authToken);
    }

    @Override
    public void terminateSession(String authToken) {
        sessions.remove(authToken);
    }

    @Override
    public void clear() {
        sessions.clear();
    }
}
