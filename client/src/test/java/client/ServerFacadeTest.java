package client;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.GameData;
import model.exceptions.HTTPException;
import server.Server;

public class ServerFacadeTest {

    private static Server server;
    static ServerFacade facade;
    private static int port;

    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clearDatabase() {
        facade = new ServerFacade("http://localhost:" + port);
        try {
            facade.clearDatabase();
        } catch (HTTPException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    @Test
    void testCreateGame() {
        try {
            facade.register("username", "email", "password");
        } catch (HTTPException e) {
            throw new RuntimeException(e.getMessage());
        }

        assertDoesNotThrow(() -> facade.createGame("newGame"));
    }

    @Test
    void testCreateGameUnauthenticated() {
        HTTPException exception = assertThrows(HTTPException.class, () -> facade.createGame("newGame"));
        assertTrue(exception.getMessage().toLowerCase().contains("unauthorized"));
    }

    @Test
    void testListGames() {
        List<String> gameNames = Arrays.asList("Game1", "Game2", "Game3");
        try {
            facade.register("username", "email", "password");
            for (String game : gameNames) {
                facade.createGame(game);
            }
            List<GameData> games = facade.listGames();
            for (GameData game : games) {
                assertTrue(gameNames.contains(game.gameName()));
            }
        } catch (HTTPException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Test
    void testListGamesNoneFound() {
        try {
            facade.register("username", "email", "password");
            List<GameData> games = facade.listGames();
            assertTrue(games.isEmpty());
        } catch (HTTPException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Test
    void testLogin() {
        try {
            facade.register("username", "email", "password");
        } catch (HTTPException e) {
            throw new RuntimeException(e.getMessage());
        }
        assertDoesNotThrow(() -> facade.login("username", "password"));
    }

    @Test
    void testLoginUnauthorized() {
        HTTPException exception = assertThrows(HTTPException.class, () -> facade.login("username", "password"));
        assertTrue(exception.getMessage().toLowerCase().contains("unauthorized"));
    }

    @Test
    void testLogout() {
        try {
            facade.register("username", "email", "password");
        } catch (HTTPException e) {
            throw new RuntimeException(e.getMessage());
        }
        assertDoesNotThrow(() -> facade.logout());
        HTTPException exception = assertThrows(HTTPException.class, () -> facade.listGames());
        assertTrue(exception.getMessage().toLowerCase().contains("unauthorized"));
    }

    @Test
    void testLogoutRepeatedly() {
        try {
            facade.register("username", "email", "password");
        } catch (HTTPException e) {
            throw new RuntimeException(e.getMessage());
        }
        for (int i = 0; i < 10; i++) {
            assertDoesNotThrow(() -> facade.logout());
        }
    }

    @Test
    void testJoinGame() {
        int gameID;
        try {
            facade.register("username", "email", "password");
            gameID = facade.createGame("gameName");
        } catch (HTTPException e) {
            throw new RuntimeException(e.getMessage());
        }
        assertDoesNotThrow(() -> facade.joinGame(gameID, "WHITE", null));
    }

    @Test
    void testJoinGameDoesNotExist() {
        try {
            facade.register("username", "email", "password");
        } catch (HTTPException e) {
            throw new RuntimeException(e.getMessage());
        }
        HTTPException exception = assertThrows(HTTPException.class, () -> facade.joinGame(0, "BLACK", null));
        assertTrue(exception.getMessage().toLowerCase().contains("bad request"));
    }

    @Test
    void testRegister() {
        assertDoesNotThrow(() -> facade.register("username", "email", "password"));
    }

    @Test
    void testRegisterBadRequest() {
        HTTPException exception = assertThrows(HTTPException.class, () -> facade.register(null, null, null));
        assertTrue(exception.getMessage().toLowerCase().contains("bad request"));
    }

    @Test
    void testClearDatabase() {
        try {
            facade.register("username", "email", "password");
            facade.clearDatabase();
        } catch (HTTPException e) {
            throw new RuntimeException(e.getMessage());
        }
        HTTPException exception = assertThrows(HTTPException.class, () -> facade.login("username", "password"));
        assertTrue(exception.getMessage().toLowerCase().contains("unauthorized"));
    }

    @Test
    void testClearDatabaseRepeatedly() {
        for (int i = 0; i < 10; i++) {
            assertDoesNotThrow(() -> facade.clearDatabase());
        }
    }

    @Test
    void testObserveGame() {
        assertDoesNotThrow(() -> facade.observeGame(0, null));
    }

    @Test
    void testObserveGameRepeatedly() {
        for (int i = 0; i < 10; i++) {
            assertDoesNotThrow(() -> facade.observeGame(0, null));
        }
    }

}
