package dataaccess.user;

import java.util.HashMap;
import java.util.Map;

import dataaccess.DataAccessException;
import model.UserData;

public class MemoryUserDAO implements UserDAO {

    Map<String, UserData> users;

    public MemoryUserDAO() {
        users = new HashMap<>();
    }

    @Override
    public UserData getUser(String username) {

        return users.get(username);
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        if (users.get(userData.username()) != null) {
            throw new DataAccessException("Error: Duplicate entry attempt");
        }
        users.put(userData.username(), userData);
    }

    @Override
    public void clear() {
        users.clear();
    }
}
