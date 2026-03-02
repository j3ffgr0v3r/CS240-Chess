package serverhandler;

import java.util.Map;

import com.google.gson.Gson;

import io.javalin.http.Context;
import requestsandresults.Register.RegisterRequest;
import requestsandresults.Register.RegisterResult;
import requestsandresults.SessionCreation.SessionCreationRequest;
import requestsandresults.SessionCreation.SessionCreationResult;
import service.UserService;

public class UserServerHandler extends ServerHandler {

    public UserServerHandler(UserService userService) {
        super(userService);
    }

    public void registerUser(Context ctx) {
        RegisterRequest body = new Gson().fromJson(ctx.body(), RegisterRequest.class);
        ctx.contentType("application/json");

        if (body == null || body.username() == null || body.password() == null || body.email() == null) {
            ctx.status(400);
            ctx.result(new Gson().toJson(Map.of("message", "Error: Please include user information in register request.")));
        } else {
            RegisterResult result = userService.register(body);

            ctx.status(result.message() != null ? 403 : 200);

            ctx.result(new Gson().toJson(result));
        }

    }

    public void loginUser(Context ctx) {
        SessionCreationRequest body = new Gson().fromJson(ctx.body(), SessionCreationRequest.class);
        ctx.contentType("application/json");

        if (body == null || body.username() == null || body.password() == null) {
            ctx.status(400);
            ctx.result(new Gson().toJson(Map.of("message", "Error: bad request")));
        } else {
            SessionCreationResult result = userService.login(body);

            ctx.status(result.message() != null ? 401 : 200);

            ctx.result(new Gson().toJson(result));
        }

    }

    public void logoutUser(Context ctx) {
        String authToken = ctx.header("authorization");
        ctx.contentType("application/json");

        if (authToken == null || !isAuthorized(authToken)) {
            ctx.status(402);
            ctx.result(new Gson().toJson(Map.of("message", "Error: You are not signed in.")));
        } else {
            String result = userService.logout(authToken);

            ctx.status(result != null ? 401 : 200);

            if (result != null) {
                ctx.result(new Gson().toJson(Map.of("message", result)));
            }
        }

    }

}
