package client;

import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

public class NotificationHandler {
    void notify(NotificationMessage notification) {
        System.out.println(notification.getMessage());
    }

    void notify(ErrorMessage notification) {
        System.out.println(notification.getErrorMessage());
    }

    void notify(LoadGameMessage notification) {
        System.out.println(notification.getGame().toString());
    }
}
