package service;

import dataaccess.Auth.AuthDAO;
import dataaccess.Game.GameDAO;
import model.AuthData;
import requestsandresults.ListGames.ListGamesResult;

public class GameService {

    GameDAO gameDAO;
    AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ListGamesResult listGames(String authToken) {
        AuthData authData = authDAO.getSession(authToken);

        if (authData == null) {
            return new ListGamesResult("Error: unauthorized", null);
        }

        return new ListGamesResult(null, gameDAO.getAllGames());
    }

	public void clear() {
        gameDAO.clear();
    }
    
}
