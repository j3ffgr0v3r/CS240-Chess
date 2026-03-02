package server;

import dataaccess.Auth.AuthDAO;
import dataaccess.Auth.MemoryAuthDAO;
import dataaccess.User.MemoryUserDAO;
import dataaccess.User.UserDAO;
import io.javalin.Javalin;
import io.javalin.http.Context;
import serverhandler.UserServerHandler;
import service.UserService;

public class Server {

    private final Javalin javalin;

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    private final UserService userService;

    public Server() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();

        userService = new UserService(userDAO, authDAO);



        javalin = Javalin.create(config -> config.staticFiles.add("web"))
            .post("/user", this::registerUser);

    }

    private void registerUser(Context ctx) {
        UserServerHandler handler = new UserServerHandler(userService);

        handler.registerUser(ctx);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
