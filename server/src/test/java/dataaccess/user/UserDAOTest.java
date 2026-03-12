package dataaccess.user;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import dataaccess.DataAccessException;
import model.UserData;

public abstract class UserDAOTest {
    protected UserDAO userDAO;

    @Test
    void testClear() throws DataAccessException {
        userDAO.createUser(new UserData("username", BCrypt.hashpw("password", BCrypt.gensalt()), "email"));
        userDAO.clear();
        assertTrue(userDAO.getUser("username") == null);
    }

    @Test
    void testCreateAndGetUser() throws DataAccessException {
        UserData userData = new UserData("username", BCrypt.hashpw("password", BCrypt.gensalt()), "email");
        userDAO.createUser(userData);
        assertTrue(userDAO.getUser("username").equals(userData));
    }

    @Test
    void testCreateUser_Duplicates() throws DataAccessException {
        UserData userData = new UserData("username", BCrypt.hashpw("password", BCrypt.gensalt()), "email");
        userDAO.createUser(userData);
        Exception exception = assertThrows(DataAccessException.class, () -> userDAO.createUser(userData));
        assertTrue(exception.getMessage().contains("Duplicate"));
    }

    @Test
    void testGetUser_NotFound() throws DataAccessException {
        assertTrue(userDAO.getUser("doesNotExist") == null);
    }
}
