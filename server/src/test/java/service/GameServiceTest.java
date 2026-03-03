package service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import chess.ChessGame;
import dataaccess.auth.AuthDAO;
import dataaccess.auth.MemoryAuthDAO;
import dataaccess.game.GameDAO;
import dataaccess.game.MemoryGameDAO;
import model.AuthData;
import model.GameData;
import requestsandresults.creategame.CreateGameRequest;
import requestsandresults.joingame.JoinGameRequest;

public class GameServiceTest {

    private AuthDAO authDAO;
    private GameDAO gameDAO;

    private GameService gameService;

    @BeforeEach
    void setUp() {
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();

        gameService = new GameService(gameDAO, authDAO);
    }

    @Test
    void testListGamesUnauthorized() {
        assertThrows(UnauthorizedException.class, () -> gameService.listGames("NotAToken"));
    }

    @Test
    void testListGames() {
        authDAO.createSession(new AuthData("authToken", "username"));

        GameData game = new GameData(123, null, null, "gameName", new ChessGame());

        gameDAO.setGame(game);

        try {
            assertTrue(gameService.listGames("authToken").games().get(0) == game);
        } catch (UnauthorizedException e) {
            fail();
        }
        
    }

    @Test
    void testCreateGameUnauthorized() {
        assertThrows(UnauthorizedException.class, () -> gameService.createGame(new CreateGameRequest("NotAToken", "gameName")));
    }

    @Test
    void testCreateGame() {
        authDAO.createSession(new AuthData("authToken", "username"));

        try {
            gameService.createGame(new CreateGameRequest("authToken", "gameName"));

            assertTrue(gameService.listGames("authToken").games().size() == 1);
        } catch (BadRequestException | UnauthorizedException e) {
            fail();
        }
        
    }

    @Test
    void testJoinGameUnauthorized() {
        assertThrows(UnauthorizedException.class, () -> gameService.joinGame(new JoinGameRequest("NotAToken", "WHITE", 123)));
    }

    @Test
    void testJoinGame() {
        authDAO.createSession(new AuthData("authToken", "username"));

        int gameID;

        try {
            gameID = gameService.createGame(new CreateGameRequest("authToken", "gameName"));

            gameService.joinGame(new JoinGameRequest("authToken", "WHITE", gameID));
            
        } catch (BadRequestException | UnauthorizedException | AlreadyTakenException e) {
            fail();
        }
    }

    @Test
    void testJoinGameAlreadyTaken() {
        authDAO.createSession(new AuthData("authToken", "username"));

        int gameID;

        try {
            gameID = gameService.createGame(new CreateGameRequest("authToken", "gameName"));

            gameService.joinGame(new JoinGameRequest("authToken", "WHITE", gameID));
            assertThrows(AlreadyTakenException.class, () -> gameService.joinGame(new JoinGameRequest("authToken", "WHITE", gameID)));
            
        } catch (BadRequestException | UnauthorizedException | AlreadyTakenException e) {
            fail();
        }
    }

    @Test
    void testClear() {
        authDAO.createSession(new AuthData("authToken", "username"));
        
        try {
            gameService.createGame(new CreateGameRequest("authToken", "gameName"));

            gameService.clear();

            assertTrue(gameDAO.getAllGames().isEmpty());
            
        } catch (BadRequestException | UnauthorizedException e) {
            fail();
        }


    }

}
