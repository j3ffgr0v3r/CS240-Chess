package dataaccess.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.GameData;

public class MemoryGameDAO implements GameDAO {

    Map<String, GameData> games;

    public MemoryGameDAO() {
        games = new HashMap<>();
    }

    
    @Override
    public List<GameData> getAllGames() {
        return new ArrayList<>(games.values());
    }


    @Override
    public void clear() {
        games.clear();
    }

}
