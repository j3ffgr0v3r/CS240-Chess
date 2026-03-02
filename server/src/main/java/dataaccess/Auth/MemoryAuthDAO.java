package dataaccess.Auth;

import java.util.HashMap;
import java.util.Map;

import model.AuthData;

public class MemoryAuthDAO implements AuthDAO {

    Map<String, AuthData> auths;

    public MemoryAuthDAO() {
        auths = new HashMap<>();
    }

    @Override
    public void createAuth(AuthData authData) {
        auths.put(authData.authToken(), authData);
    }

    @Override
    public void clear() {
        auths.clear();
    }
}
