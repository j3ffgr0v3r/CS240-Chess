package dataaccess.Auth;

import model.AuthData;

public interface AuthDAO {

    public void createSession(AuthData authData);

    public AuthData getSession(String authData);

    public void terminateSession(String authData);

    public void clear();
}
