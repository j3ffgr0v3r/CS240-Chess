package dataaccess.game;

import java.util.List;

import dataaccess.DataAccessException;
import model.GameData;

public class MySQLGameDAO implements GameDAO {

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
