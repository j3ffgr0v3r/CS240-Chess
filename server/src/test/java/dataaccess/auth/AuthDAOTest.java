package dataaccess.auth;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import dataaccess.DataAccessException;
import model.AuthData;

public abstract class AuthDAOTest {
    protected AuthDAO authDAO;

    @Test
    void testClear() throws DataAccessException {
        authDAO.createSession(new AuthData("authToken", "username"));
        authDAO.clear();
        assertTrue(authDAO.getSession("authToken") == null);
    }

    @Test
    void testCreateAndGetSession() throws DataAccessException {
        AuthData authData = new AuthData("authToken", "username");
        authDAO.createSession(authData);
        assertTrue(authDAO.getSession("authToken").equals(authData));
    }

    @Test
    void testCreateSessionUserDuplicates() throws DataAccessException {
        AuthData authData = new AuthData("authToken", "username");
        authDAO.createSession(authData);
        Exception exception = assertThrows(DataAccessException.class, () -> authDAO.createSession(authData));
        assertTrue(exception.getMessage().contains("Duplicate"));
    }

    @Test
    void testGetSessionUserNotFound() throws DataAccessException {
        assertTrue(authDAO.getSession("doesNotExist") == null);
    }

    @Test
    void testTerminateSession() throws DataAccessException {
        authDAO.createSession(new AuthData("authToken", "username"));
        authDAO.terminateSession("authToken");
        assertTrue(authDAO.getSession("authToken") == null);
    }
}
