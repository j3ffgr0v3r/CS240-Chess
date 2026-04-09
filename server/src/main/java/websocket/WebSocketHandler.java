package websocket;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;

import com.google.gson.Gson;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.InvalidMoveException;
import dataaccess.DataAccessException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.GameData;
import model.exceptions.BadRequestException;
import model.exceptions.UnauthorizedException;
import service.GameService;
import service.UserService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();

    private final UserService userService;
    private final GameService gameService;

    public WebSocketHandler(UserService userService, GameService gameService) {
        super();
        this.userService = userService;
        this.gameService = gameService;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand userGameCommand = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            String username = userService.getUserFromAuth(userGameCommand.getAuthToken());
            switch (userGameCommand.getCommandType()) {
                case CONNECT -> connect(userGameCommand.getGameID(), username, ctx.session);
                case MAKE_MOVE -> {
                    MakeMoveCommand makeMoveCommand = new Gson().fromJson(ctx.message(), MakeMoveCommand.class);
                    makeMove(userGameCommand.getGameID(), username, makeMoveCommand.getMove(), ctx.session);
                }
                case LEAVE -> leave(userGameCommand.getGameID(), username, ctx.session);
                case RESIGN -> resign(userGameCommand.getGameID(), username, ctx.session);
            }
        } catch (UnauthorizedException | IOException | DataAccessException ex) {
            ErrorMessage errorMessage = new ErrorMessage(ex.getMessage());
            try {
                connections.dm(ctx.session, errorMessage);
            } catch (IOException ex2) {
                ex2.printStackTrace();
            }
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(int gameID, String playerName, Session session) throws IOException {
        connections.add(session);

        LoadGameMessage gameUpdate;
        try {
            gameUpdate = new LoadGameMessage(gameService.getGame(gameID).game());
            connections.dm(session, gameUpdate);
            String message = String.format("%s has joined the game", playerName);
            NotificationMessage serverMessage = new NotificationMessage(message);
            connections.broadcast(session, serverMessage);
        } catch (BadRequestException | DataAccessException ex) {
            ErrorMessage errorMessage = new ErrorMessage(ex.getMessage());
            connections.dm(session, errorMessage);
        }
    }

    private void leave(int gameID, String playerName, Session session) throws IOException {
        String message = String.format("%s has left the game", playerName);
        NotificationMessage serverMessage = new NotificationMessage(message);
        connections.broadcast(session, serverMessage);
        connections.remove(session);
        try {
            gameService.leaveGame(gameID, playerName);
            LoadGameMessage gameUpdate = new LoadGameMessage(gameService.getGame(gameID).game());
            connections.broadcast(session, gameUpdate);
        } catch (BadRequestException | DataAccessException ex) {
            ErrorMessage errorMessage = new ErrorMessage(ex.getMessage());
            connections.broadcast(null, errorMessage);
        }
    }

    private void resign(int gameID, String playerName, Session session) throws IOException {
        try {
            GameData game = gameService.getGame(gameID);
            
            if (game.game().getTeamTurn() == ChessGame.TeamColor.GAMEOVER || (!game.blackUsername().equals(playerName) && !game.whiteUsername().equals(playerName))) {
                throw new BadRequestException();
            } 

            String message = String.format("%s has resigned from the game.", playerName);
            NotificationMessage serverMessage = new NotificationMessage(message);
            connections.broadcast(null, serverMessage);

            ChessGame endedGame = gameService.getGame(gameID).game();
            endedGame.endGame();
            gameService.updateGame(gameID, endedGame);
        } catch (BadRequestException | DataAccessException ex) {
            ErrorMessage errorMessage = new ErrorMessage(ex.getMessage());
            connections.dm(session, errorMessage);
        }
    }

    public void makeMove(int gameID, String playerName, ChessMove move, Session session) throws IOException {
        try {
            GameData game = gameService.getGame(gameID);

            if (game.game().getTeamTurn() == ChessGame.TeamColor.GAMEOVER) {
                throw new InvalidMoveException("The game is over! You cannot move any pieces!");
            }

            ChessPiece piece = game.game().getBoard().getPiece(move.getStartPosition());
            ChessGame.TeamColor team = piece == null ? null : piece.getTeamColor();

            if (team == null || (team == ChessGame.TeamColor.BLACK && !game.blackUsername().equals(playerName))
                    || (team == ChessGame.TeamColor.WHITE && !game.whiteUsername().equals(playerName))) {
                throw new InvalidMoveException("You do not own that piece!");
            }

            gameService.makeMove(gameID, move);

            String message = String.format("%s moved %s", playerName, move);
            NotificationMessage serverMessage = new NotificationMessage(message);
            connections.broadcast(session, serverMessage);

            LoadGameMessage gameUpdate = new LoadGameMessage(gameService.getGame(gameID).game());
            connections.broadcast(null, gameUpdate);

            if (gameService.getGame(gameID).game().getTeamTurn() == ChessGame.TeamColor.GAMEOVER) {
                serverMessage = new NotificationMessage("Game Over!");
                connections.broadcast(null, serverMessage);
            }
        } catch (BadRequestException | InvalidMoveException | DataAccessException ex) {
            ErrorMessage errorMessage = new ErrorMessage(ex.getMessage());
            connections.dm(session, errorMessage);
        }
    }
}
