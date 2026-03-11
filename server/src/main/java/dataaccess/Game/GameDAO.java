package dataaccess.game;

import java.util.List;

import dataaccess.DataAccessException;
import model.GameData;

public interface GameDAO {

    public List<GameData> getAllGames() throws DataAccessException;

    public void setGame(GameData newGame) throws DataAccessException;

    public GameData getGame(int gameID) throws DataAccessException;

    public void clear() throws DataAccessException;
}
