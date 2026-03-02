package serverhandler;

import java.util.Map;

import com.google.gson.Gson;

import io.javalin.http.Context;
import requestsandresults.Register.RegisterRequest;
import requestsandresults.Register.RegisterResult;
import requestsandresults.SessionCreation.SessionCreationRequest;
import requestsandresults.SessionCreation.SessionCreationResult;
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
        RegisterRequest registerRequest = new Gson().fromJson(ctx.body(), RegisterRequest.class);
        ctx.contentType("application/json");

        try {
            RegisterResult result = userService.register(registerRequest);

            ctx.status(200);
            ctx.result(new Gson().toJson(result));
        } catch (BadRequestException | AlreadyTakenException e) {
            ctx.status(e.getStatusCode());
            ctx.result(new Gson().toJson(Map.of("message", e)));
        }

    }

    public void loginUser(Context ctx) {
        SessionCreationRequest body = new Gson().fromJson(ctx.body(), SessionCreationRequest.class);
        ctx.contentType("application/json");

        try {
            SessionCreationResult result = userService.login(body);

            ctx.status(200);
            ctx.result(new Gson().toJson(result));
        } catch (BadRequestException | UnauthorizedException e) {
            ctx.status(e.getStatusCode());
            ctx.result(new Gson().toJson(Map.of("message", e)));
        }

    }

    public void logoutUser(Context ctx) {
        String authToken = ctx.header("authorization");
        ctx.contentType("application/json");

        try {
            String result = userService.logout(authToken);

            ctx.status(200);
            ctx.result(new Gson().toJson(Map.of("message", result)));            
        } catch (BadRequestException | UnauthorizedException e) {
            ctx.status(e.getStatusCode());
            ctx.result(new Gson().toJson(Map.of("message", e)));
        }
    }

}
