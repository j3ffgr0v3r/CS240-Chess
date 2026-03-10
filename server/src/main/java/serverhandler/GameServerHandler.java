package serverhandler;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import requestsandresults.creategame.CreateGameRequest;
import requestsandresults.joingame.JoinGameRequest;
import requestsandresults.listgames.ListGamesResult;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.GameService;
import service.UnauthorizedException;

public class GameServerHandler {

    private final GameService gameService;

    public GameServerHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void listGames(Context ctx) {
        String authToken = ctx.header("authorization");
        ctx.contentType("application/json");

        try {
            ListGamesResult result = gameService.listGames(authToken);

            ctx.status(200);
            ctx.result(new Gson().toJson(result));
        } catch (UnauthorizedException | DataAccessException e) {
            ctx.status(e.getStatusCode());
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        }
    }

    public void createGame(Context ctx) {
        String authToken = ctx.header("authorization");
        JsonObject body = new Gson().fromJson(ctx.body(), JsonObject.class);
        String gameName = body.isEmpty() ? null : body.get("gameName").getAsString();

        ctx.contentType("application/json");

        try {
            int gameID = gameService.createGame(new CreateGameRequest(authToken, gameName));

            ctx.status(200);
            ctx.result(new Gson().toJson(Map.of("gameID", gameID)));
        } catch (BadRequestException | UnauthorizedException | DataAccessException e) {
            ctx.status(e.getStatusCode());
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        }
    }

    public void joinGame(Context ctx) {
        String authToken = ctx.header("authorization");
        JsonObject body = new Gson().fromJson(ctx.body(), JsonObject.class);
        body.addProperty("authToken", authToken);
        JoinGameRequest request = new Gson().fromJson(body, JoinGameRequest.class);

        ctx.contentType("application/json");

        try {
            gameService.joinGame(request);
            ctx.status(200);
        } catch (BadRequestException | UnauthorizedException | AlreadyTakenException | DataAccessException e) {
            ctx.status(e.getStatusCode());
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        }
    }
}
