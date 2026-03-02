package service;

import java.util.Random;

import chess.ChessGame;
import dataaccess.Game.GameDAO;
import model.GameData;
import requestsandresults.JoinGame.JoinGameRequest;
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

        gameDAO.setGame(new GameData(gameID, null, null, gameName, new ChessGame()));

        return gameID;
    }

    public boolean joinGame(JoinGameRequest request) {
        GameData game = gameDAO.getGame(request.gameID());

        GameData updatedGame;

        if ("WHITE".equals(request.playerColor()) && game.whiteUsername() != null) {
            updatedGame = new GameData(game.gameID(), "username", game.blackUsername(), game.gameName(), game.game());
        } else if ("BLACK".equals(request.playerColor()) && game.blackUsername() != null) {
            updatedGame = new GameData(game.gameID(), game.whiteUsername(), "username", game.gameName(), game.game());
        } else {
            return false;
        }

        gameDAO.setGame(updatedGame);
        return true;
    }

	public void clear() {
        gameDAO.clear();
    }
    
}
