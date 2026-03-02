package dataaccess.game;

import java.util.List;

import model.GameData;

public interface GameDAO {

    public void clear();

    public List<GameData> getAllGames();

    public void setGame(GameData newGame);

    public GameData getGame(int gameID);
}
