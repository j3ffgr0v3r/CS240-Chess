package dataaccess.game;

import java.util.List;

import com.google.gson.Gson;

import dataaccess.DataAccessException;
import dataaccess.MySQLDAO;
import model.GameData;

public class MySQLGameDAO extends MySQLDAO implements GameDAO {

    private final String[] createStatements = {
            """
                        CREATE TABLE IF NOT EXISTS games (
                            `gameID` INT NOT NULL,
                            `gameData` TEXT DEFAULT NULL,
                            PRIMARY KEY (`gameID`),
                            INDEX(gameID)
                        )
                    """
    };

    public MySQLGameDAO() throws DataAccessException {
        configureDatabase(createStatements);
    }

    @Override
    public List<GameData> getAllGames() throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setGame(GameData newGame) throws DataAccessException {
        executeUpdate("INSERT INTO games (gameID, gameData) VALUES (?, ?);", newGame.gameID(), newGame);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String output = executeQuery(rs -> {
            return rs.getString("gameData");
        }, "SELECT gameData FROM games WHERE gameID = ?;", gameID);
        return new Gson().fromJson(output, GameData.class);
    }

    @Override
    public void clear() throws DataAccessException {
        executeUpdate("TRUNCATE TABLE games;");
    }
}
