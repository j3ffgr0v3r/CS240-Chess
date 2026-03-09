package dataaccess.auth;

import model.AuthData;

public class MySQLAuthDAO implements AuthDAO {

    @Override
    public void createSession(AuthData authData) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public AuthData getSession(String authData) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void terminateSession(String authData) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
