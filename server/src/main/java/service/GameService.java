package service;

import java.util.Random;

import chess.ChessGame;
import dataaccess.Game.GameDAO;
import model.GameData;
import requestsandresults.ListGames.ListGamesResult;

public class GameService {

    GameDAO gameDAO;

    public GameService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public ListGamesResult listGames() {
        return new ListGamesResult(null, gameDAO.getAllGames());
    }

    public int createGame(String gameName) {
        int gameID = (new Random()).nextInt();

        gameDAO.addGame(new GameData(gameID, null, null, gameName, new ChessGame()));

        return gameID;
    }

	public void clear() {
        gameDAO.clear();
    }
    
}
