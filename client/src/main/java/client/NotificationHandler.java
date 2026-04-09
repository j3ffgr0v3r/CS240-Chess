package client;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import ui.ChessBoard;
import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;
import static ui.EscapeSequences.SET_TEXT_COLOR_YELLOW;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public class NotificationHandler {

    List<String> messages = new ArrayList<>(8);

    ui.ChessBoard board;

    public NotificationHandler(ChessBoard board) {
        this.board = board;
    }

    void notify(String message) {
        ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
        switch (serverMessage.getServerMessageType()) {
                case NOTIFICATION -> notification(new Gson().fromJson(message, NotificationMessage.class));
                case ERROR -> error(new Gson().fromJson(message, ErrorMessage.class));
                case LOAD_GAME -> loadGame(new Gson().fromJson(message, LoadGameMessage.class));
            }
    }

    void notification(NotificationMessage notification) {
        addMessage(String.format("%s%s%s", SET_TEXT_COLOR_YELLOW, notification.getMessage(), RESET_TEXT_COLOR));
    }

    void error(ErrorMessage errorMessage) {
        System.out.println(String.format("%s%s%s", SET_TEXT_COLOR_RED, errorMessage.getErrorMessage(), RESET_TEXT_COLOR));
    }

    void loadGame(LoadGameMessage message) {
        board.updateGame(message.getGame());
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
