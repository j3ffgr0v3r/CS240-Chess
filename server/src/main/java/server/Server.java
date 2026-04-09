package server;

import dataaccess.DataAccessException;
import dataaccess.auth.AuthDAO;
import dataaccess.auth.MySQLAuthDAO;
import dataaccess.game.GameDAO;
import dataaccess.game.MySQLGameDAO;
import dataaccess.user.MySQLUserDAO;
import dataaccess.user.UserDAO;
import io.javalin.Javalin;
import io.javalin.http.Context;
import serverhandler.ApplicationServerHandler;
import serverhandler.GameServerHandler;
import serverhandler.UserServerHandler;
import service.GameService;
import service.UserService;
import websocket.WebSocketHandler;

public class Server {
    private final Javalin javalin;
    private final WebSocketHandler webSocketHandler;

    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    private final UserService userService;
    private final GameService gameService;

    public Server() {
        try {
            authDAO = new MySQLAuthDAO();
            userDAO = new MySQLUserDAO();
            gameDAO = new MySQLGameDAO();

            userService = new UserService(userDAO, authDAO);
            gameService = new GameService(gameDAO, authDAO);
            webSocketHandler = new WebSocketHandler();

            javalin = Javalin.create(config -> config.staticFiles.add("web"))
                    .post("/user", this::registerUser)
                    .post("/session", this::loginUser)
                    .delete("/session", this::logoutUser)
                    .get("/game", this::listGames)
                    .post("/game", this::createGame)
                    .put("/game", this::joinGame)
                    .delete("/db", this::clearApplication)
                    .ws("/ws", ws -> {
                        ws.onConnect(webSocketHandler);
                        ws.onMessage(webSocketHandler);
                        ws.onClose(webSocketHandler);
                    });
        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void registerUser(Context ctx) {
        UserServerHandler handler = new UserServerHandler(userService);

        handler.registerUser(ctx);
    }

    private void loginUser(Context ctx) {
        UserServerHandler handler = new UserServerHandler(userService);

        handler.loginUser(ctx);
    }

    private void logoutUser(Context ctx) {
        UserServerHandler handler = new UserServerHandler(userService);

        handler.logoutUser(ctx);
    }

    private void listGames(Context ctx) {
        GameServerHandler handler = new GameServerHandler(gameService);

        handler.listGames(ctx);
    }

    private void createGame(Context ctx) {
        GameServerHandler handler = new GameServerHandler(gameService);

        handler.createGame(ctx);
    }

    private void joinGame(Context ctx) {
        GameServerHandler handler = new GameServerHandler(gameService);

        handler.joinGame(ctx);
    }

    private void clearApplication(Context ctx) {
        ApplicationServerHandler handler = new ApplicationServerHandler(userService, gameService);

        handler.clearApplication(ctx);
    }

}
