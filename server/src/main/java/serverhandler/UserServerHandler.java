package serverhandler;

import java.util.Map;

import com.google.gson.Gson;

import io.javalin.http.Context;
import requestsandresults.RegisterRequest;
import requestsandresults.RegisterResult;
import service.UserService;

public class UserServerHandler {

    private final UserService service;

    public UserServerHandler(UserService service) {
        this.service = service;
    }
    
    public void registerUser(Context ctx) {
        RegisterRequest body = new Gson().fromJson(ctx.body(), RegisterRequest.class);
        ctx.contentType("application/json");

        if (body == null) {
            ctx.status(400);
            ctx.result(new Gson().toJson(Map.of("message", "Please include user information in register request.")));
        } else {
            RegisterResult result = service.register(body);

            ctx.status(result.message() != null ? 403 : 200);

            ctx.result(new Gson().toJson(result));
        }

    }

}
