package serverhandler;

import java.util.Map;

import com.google.gson.Gson;

import io.javalin.http.Context;
import requestsandresults.ListGames.ListGamesResult;
import service.GameService;

public class GameServerHandler {
    private final GameService gameService;

    public GameServerHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void listGames(Context ctx) {
        String authToken = ctx.header("authorization");
        ctx.contentType("application/json");

        if (authToken == null) {
            ctx.status(401);
            ctx.result(new Gson().toJson(Map.of("message", "Error: unauthorized")));
        } else {
            ListGamesResult result = gameService.listGames(authToken);

            ctx.status(result != null ? 401 : 200);

            if (result != null) {
                ctx.result(new Gson().toJson(result));
            }
        }

    }
}
