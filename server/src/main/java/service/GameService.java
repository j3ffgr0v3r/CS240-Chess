package service;

import dataaccess.Game.GameDAO;
import requestsandresults.ListGames.ListGamesResult;

public class GameService {

    GameDAO gameDAO;

    public GameService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public ListGamesResult listGames(String authToken) {
        return new ListGamesResult(null, gameDAO.getAllGames());
    }

	public void clear() {
        gameDAO.clear();
    }
    
}
