package serverhandler;

import java.util.Map;

import com.google.gson.Gson;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.GameService;
import service.UserService;

public class ApplicationServerHandler {

    private final UserService userService;
    private final GameService gameService;

    public ApplicationServerHandler(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }

    public void clearApplication(Context ctx) {
        try {
            userService.clear();
            gameService.clear();

            ctx.contentType("application/json");
            ctx.status(200);
        } catch (DataAccessException e) {
            ctx.status(e.getStatusCode());
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        }
    }
}
