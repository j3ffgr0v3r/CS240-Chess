package dataaccess.game;

import java.util.List;

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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clear() throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
