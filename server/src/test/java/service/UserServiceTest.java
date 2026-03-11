package service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import dataaccess.DataAccessException;
import dataaccess.auth.AuthDAO;
import dataaccess.auth.MemoryAuthDAO;
import dataaccess.user.MemoryUserDAO;
import dataaccess.user.UserDAO;
import model.AuthData;
import model.UserData;
import requestsandresults.register.RegisterRequest;
import requestsandresults.sessioncreation.SessionCreationRequest;

public class UserServiceTest {

    private AuthDAO authDAO;
    private UserDAO userDAO;

    private UserService userService;

    @BeforeEach
    void setUp() {
        authDAO = new MemoryAuthDAO();
        userDAO = new MemoryUserDAO();

        userService = new UserService(userDAO, authDAO);
    }

    @Test
    void testRegisterAlreadyTaken() throws DataAccessException {
        try {
            userService.register(new RegisterRequest("username", "email", "password"));

            assertThrows(AlreadyTakenException.class, () -> userService.register(new RegisterRequest("username", "email", "password")));

        } catch (BadRequestException | AlreadyTakenException e) {
            fail();
        }
    }

    @Test
    void testRegister() throws DataAccessException {
        try {
            String authToken = userService.register(new RegisterRequest("username", "email", "password")).authToken();

            assertTrue(authDAO.getSession(authToken) instanceof AuthData);

        } catch (BadRequestException | AlreadyTakenException e) {
            fail();
        }
    }

    @Test
    void testLogoutUnauthorized() {
        assertThrows(UnauthorizedException.class, () -> userService.logout("NotAToken"));
    }

    @Test
    void testLogout() throws DataAccessException {
        try {
            String authToken = userService.register(new RegisterRequest("username", "email", "password")).authToken();

            userService.logout(authToken);

            assertTrue(authDAO.getSession(authToken) == null);

        } catch (BadRequestException | AlreadyTakenException | UnauthorizedException e) {
            fail();
        }
    }

    @Test
    void testLoginUnauthorized() {
        assertThrows(UnauthorizedException.class, () -> userService.login(new SessionCreationRequest("username", "password")));
    }

    @Test
    void testLogin() throws DataAccessException {
        try {
            userDAO.createUser(new UserData("username", BCrypt.hashpw("password", BCrypt.gensalt()), "email"));

            assertTrue(userService.login(new SessionCreationRequest("username", "password")).username() == "username");

        } catch (BadRequestException | UnauthorizedException e) {
            fail();
        }
    }

    @Test
    void testClear() throws DataAccessException {
        try {
            String authToken = userService.register(new RegisterRequest("username", "email", "password")).authToken();

            userService.clear();

            assertTrue(userDAO.getUser("username") == null);
            assertTrue(authDAO.getSession(authToken) == null);

        } catch (BadRequestException | AlreadyTakenException e) {
            fail();
        }
    }
}
