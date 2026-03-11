package dataaccess.user;

import dataaccess.DataAccessException;
import model.UserData;

public interface UserDAO {

    public UserData getUser(String username) throws DataAccessException;

    public void createUser(UserData userData) throws DataAccessException;

    public void clear() throws DataAccessException;
}
