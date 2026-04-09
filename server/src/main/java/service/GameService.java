package service;

import java.util.Random;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.DataAccessException;
import dataaccess.auth.AuthDAO;
import dataaccess.game.GameDAO;
import model.GameData;
import model.exceptions.AlreadyTakenException;
import model.exceptions.BadRequestException;
import model.exceptions.UnauthorizedException;
import model.requests.CreateGameRequest;
import model.requests.JoinGameRequest;
import model.results.GameCreationResult;
import model.results.ListGamesResult;

public class GameService extends Service {

    GameDAO gameDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) throws DataAccessException {
        super(authDAO);
        this.gameDAO = gameDAO;
    }

    public ListGamesResult listGames(String authToken) throws UnauthorizedException, DataAccessException {
        isAuthorized(authToken);

        return new ListGamesResult(gameDAO.getAllGames());
    }

    public GameCreationResult createGame(CreateGameRequest request) throws BadRequestException, UnauthorizedException, DataAccessException {
        if (request.gameName() == null) {
            throw new BadRequestException();
        }

        isAuthorized(request.authToken());

        int gameID = (new Random()).nextInt(Integer.MAX_VALUE - 1) + 1;

        gameDAO.setGame(new GameData(gameID, null, null, request.gameName(), new ChessGame()));

        return new GameCreationResult(gameID);
    }

    public boolean joinGame(JoinGameRequest request) throws BadRequestException, UnauthorizedException, AlreadyTakenException, DataAccessException {
        if ((!"WHITE".equals(request.playerColor()) && !"BLACK".equals(request.playerColor())) || request.gameID() == 0) {
            throw new BadRequestException();
        }
        isAuthorized(request.authToken());

        GameData game = gameDAO.getGame(request.gameID());
        String username = authDAO.getSession(request.authToken()).username();

        GameData updatedGame;

        if ("WHITE".equals(request.playerColor())) {
            if (game.whiteUsername() != null) {
                throw new AlreadyTakenException();
            }
            updatedGame = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        } else {
            if (game.blackUsername() != null) {
                throw new AlreadyTakenException();
            }
            updatedGame = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
        }

        gameDAO.setGame(updatedGame);
        return true;
    }

    public void leaveGame(int gameID, String username) throws DataAccessException  {
        GameData game = gameDAO.getGame(gameID);

        GameData updatedGame = game;

        if (game.whiteUsername().equals(username)) {
            updatedGame = new GameData(game.gameID(), null, game.blackUsername(), game.gameName(), game.game());
        }
        if (game.blackUsername().equals(username)) {
            updatedGame = new GameData(game.gameID(), game.whiteUsername(), null, game.gameName(), game.game());
        }

        gameDAO.setGame(updatedGame);
    }

    public GameData getGame(int gameID) throws DataAccessException {
        return gameDAO.getGame(gameID);
    }

    public void makeMove(int gameID, ChessMove move) throws InvalidMoveException, DataAccessException {
        GameData game = gameDAO.getGame(gameID);

        ChessGame chessGame = game.game();

        chessGame.makeMove(move);
    
        gameDAO.setGame(new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), chessGame));
    }

    private void updateGame(int gameID, ChessGame update) throws DataAccessException {
        GameData game = gameDAO.getGame(gameID);
    
        gameDAO.setGame(new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), update));
    }

    public void clear() throws DataAccessException {
        gameDAO.clear();
    }

}
