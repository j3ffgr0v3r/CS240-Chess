package dataaccess.auth;

import java.util.HashMap;
import java.util.Map;

import model.AuthData;

public class MemoryAuthDAO implements AuthDAO {

    Map<String, AuthData> sessions;

    public MemoryAuthDAO() {
        sessions = new HashMap<>();
    }

    @Override
    public void createSession(AuthData authData) {
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
