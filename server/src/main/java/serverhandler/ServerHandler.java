package serverhandler;

import service.UserService;

public abstract class ServerHandler {

    protected final UserService userService;

    public ServerHandler(UserService userService) {
        this.userService = userService;
    }

    public boolean isAuthorized(String authToken) {
        return userService.isAuthorized(authToken);
    }
}
