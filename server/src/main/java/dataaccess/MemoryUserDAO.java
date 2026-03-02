package dataaccess;

import java.util.HashMap;
import java.util.Map;

import model.UserData;

public class MemoryUserDAO implements UserDAO {

    Map<String, UserData> users = new HashMap<>();

    public MemoryUserDAO() {

    }

    @Override
    public UserData getUser(String username)  {
        return users.get(username);
    };

    @Override
    public void createUser(UserData userData) {
        users.put(userData.username(), userData);
    }
}
