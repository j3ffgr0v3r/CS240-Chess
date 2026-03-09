package dataaccess.user;

import model.UserData;

public class MySQLUserDAO implements UserDAO {

    @Override
    public UserData getUser(String username) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void createUser(UserData userData) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
