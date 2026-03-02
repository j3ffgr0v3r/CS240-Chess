package server;

import io.javalin.Javalin;
import io.javalin.http.Context;
import serverhandler.UserServerHandler;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
            .post("/user", this::registerUser);

    }

    private void registerUser(Context ctx) {
        UserServerHandler handler = new UserServerHandler();

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
