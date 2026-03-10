package serverhandler;

import java.util.Map;

import com.google.gson.Gson;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import requestsandresults.register.RegisterRequest;
import requestsandresults.register.RegisterResult;
import requestsandresults.sessioncreation.SessionCreationRequest;
import requestsandresults.sessioncreation.SessionCreationResult;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.UnauthorizedException;
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
