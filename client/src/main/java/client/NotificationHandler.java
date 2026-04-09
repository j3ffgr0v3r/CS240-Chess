package client;

import java.util.ArrayList;
import java.util.List;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_YELLOW;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

public class NotificationHandler {

    List<String> messages = new ArrayList<>(8);

    void notify(NotificationMessage notification) {
        addMessage(String.format("%s%s%s", SET_TEXT_COLOR_YELLOW, notification.getMessage(), RESET_TEXT_COLOR));
    }

    void notify(ErrorMessage notification) {
        System.out.println(notification.getErrorMessage());
    }

    void notify(LoadGameMessage notification) {
        System.out.println(notification.getGame().toString());
    }


    private void addMessage(String message) {
        if (messages.size() >= 7) {
            messages.subList(6, messages.size()).clear();
        }
        messages.add(0, message);
    }

    public String getNotification(int index) {
        if (index < 0 || index > 8) {
            throw new IndexOutOfBoundsException(String.format("Index %d out of bounds for length 8", index));
        }
        return index >= messages.size() ? "" : messages.get(index);
    }
}
