package serverhandler;

import java.util.Map;

import com.google.gson.Gson;

import io.javalin.http.Context;
import requestsandresults.RegisterRequest;
import requestsandresults.RegisterResult;
import service.UserService;

public class UserServerHandler {

    public UserServerHandler() {

    }
    
    public void registerUser(Context ctx) {
        RegisterRequest body = new Gson().fromJson(ctx.body(), RegisterRequest.class);

        if (body == null) {
            ctx.contentType("application/json");
            ctx.status(400);
            ctx.result(new Gson().toJson(Map.of("msg", "Please include user information in register request.")));
            return;
        }

        RegisterResult result = new UserService().register(body);

    }

}
