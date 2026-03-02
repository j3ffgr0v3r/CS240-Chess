package serverhandler;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.javalin.http.Context;
import requestsandresults.ListGames.ListGamesResult;
import service.GameService;
import service.UserService;

public class GameServerHandler extends ServerHandler {
    private final GameService gameService;

    public GameServerHandler(UserService userService, GameService gameService) {
        super(userService);
        this.gameService = gameService;
    }

    public void listGames(Context ctx) {
        String authToken = ctx.header("authorization");
        ctx.contentType("application/json");

        if (authToken == null || !isAuthorized(authToken)) {
            ctx.status(401);
            ctx.result(new Gson().toJson(Map.of("message", "Error: unauthorized")));
        } else {
            ListGamesResult result = gameService.listGames();

            ctx.status(200);
            ctx.result(new Gson().toJson(result));
        }
    }

    public void createGame(Context ctx) {
        String authToken = ctx.header("authorization");
        JsonObject body = new Gson().fromJson(ctx.body(), JsonObject.class);
        String gameName = body.isEmpty() ? null : body.get("gameName").getAsString();

        ctx.contentType("application/json");

        if (gameName == null) {
            ctx.status(400);
            ctx.result(new Gson().toJson(Map.of("message", "Error: bad request")));
        } else if (authToken == null || !isAuthorized(authToken)) {
            ctx.status(401);
            ctx.result(new Gson().toJson(Map.of("message", "Error: unauthorized")));
        } else  {
            int gameID = gameService.createGame(gameName);

            ctx.status(200);
            ctx.result(new Gson().toJson(Map.of("gameID", gameID)));
        }
    }
}
