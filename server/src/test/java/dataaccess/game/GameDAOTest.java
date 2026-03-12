package dataaccess.game;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import dataaccess.DataAccessException;
import model.GameData;

public abstract class GameDAOTest {
    protected GameDAO gameDAO;

    @Test
    void testClear() throws DataAccessException {
        gameDAO.setGame(new GameData(42, "white", "black", "gameName", null));
        gameDAO.clear();
        assertTrue(gameDAO.getGame(42) == null);
    }

    @Test
    void testSetAndGetGame() throws DataAccessException {
        GameData gameData = new GameData(42, "white", "black", "gameName", null);
        gameDAO.setGame(gameData);
        assertTrue(gameDAO.getGame(42).equals(gameData));
    }

    @Test
    void testUpdateGame() throws DataAccessException {
        GameData gameData = new GameData(42, "white", "black", "gameName", null);
        gameDAO.setGame(gameData);
        assertTrue(gameDAO.getGame(42).equals(gameData));
        GameData newGameData = new GameData(42, "newWhite", "newBlack", "newGameName", null);
        gameDAO.setGame(newGameData);
        assertTrue(gameDAO.getGame(42).equals(newGameData));
    }

    @Test
    void testGetGame_NotFound() throws DataAccessException {
        assertTrue(gameDAO.getGame(-1) == null);
    }
}
