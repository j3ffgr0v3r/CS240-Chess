package serverhandler;

import java.util.Map;

import com.google.gson.Gson;

import io.javalin.http.Context;

public class UserServerHandler {

    public UserServerHandler() {

    }

    public record RegisterRequest(String username, String password, String email) {}
    
    public void registerUser(Context ctx) {
        RegisterRequest body = new Gson().fromJson(ctx.body(), RegisterRequest.class);

        if (body == null) {
            ctx.contentType("application/json");
            ctx.status(400);
            ctx.result(new Gson().toJson(Map.of("msg", "Please include user information in register request.")));
            return;
        }
    }

}
