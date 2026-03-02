package server;

import dataaccess.Auth.AuthDAO;
import dataaccess.Auth.MemoryAuthDAO;
import dataaccess.Game.GameDAO;
import dataaccess.Game.MemoryGameDAO;
import dataaccess.User.MemoryUserDAO;
import dataaccess.User.UserDAO;
import io.javalin.Javalin;
import io.javalin.http.Context;
import serverhandler.ApplicationServerHandler;
import serverhandler.UserServerHandler;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;

    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    private final UserService userService;
    private final GameService gameService;

    public Server() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();

        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::registerUser)
                .delete("/db", this::clearApplication);

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

    private void clearApplication(Context ctx) {
        ApplicationServerHandler handler = new ApplicationServerHandler(userService, gameService);

        handler.clearApplication(ctx);
    }

}
