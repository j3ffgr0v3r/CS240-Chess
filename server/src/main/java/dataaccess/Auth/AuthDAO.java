package dataaccess.Auth;

import model.AuthData;

public interface AuthDAO {
    public void createAuth(AuthData authData);

    public void clear();
}
