package serverhandler;

import java.util.Map;

import com.google.gson.Gson;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import model.exceptions.AlreadyTakenException;
import model.exceptions.BadRequestException;
import model.exceptions.UnauthorizedException;
import model.requests.RegisterRequest;
import model.requests.SessionCreationRequest;
import model.results.RegisterResult;
import model.results.SessionCreationResult;
import service.UserService;

public class UserServerHandler {

    UserService userService;

    public UserServerHandler(UserService userService) {
        this.userService = userService;
    }

    public void registerUser(Context ctx) {
        RegisterRequest request = new Gson().fromJson(ctx.body(), RegisterRequest.class);
        ctx.contentType("application/json");

        try {
            RegisterResult result = userService.register(request);

            ctx.status(200);
            ctx.result(new Gson().toJson(result));
        } catch (BadRequestException | AlreadyTakenException | DataAccessException e) {
            ctx.status(e.getStatusCode());
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        }

    }

    public void loginUser(Context ctx) {
        SessionCreationRequest request = new Gson().fromJson(ctx.body(), SessionCreationRequest.class);
        ctx.contentType("application/json");

        try {
            SessionCreationResult result = userService.login(request);

            ctx.status(200);
            ctx.result(new Gson().toJson(result));
        } catch (BadRequestException | UnauthorizedException | DataAccessException e) {
            ctx.status(e.getStatusCode());
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        }

    }

    public void logoutUser(Context ctx) {
        String authToken = ctx.header("authorization");
        ctx.contentType("application/json");

        try {
            userService.logout(authToken);

            ctx.status(200);
        } catch (BadRequestException | UnauthorizedException | DataAccessException e) {
            ctx.status(e.getStatusCode());
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        }
    }

}
