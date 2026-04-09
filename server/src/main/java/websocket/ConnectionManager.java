package websocket;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session;

import com.google.gson.Gson;

import websocket.messages.ServerMessage;

public class ConnectionManager {
    public final ConcurrentHashMap<Session, Session> connections = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Session, Integer> gameLobbies = new ConcurrentHashMap<>();

    public void add(Session session, int gameID) {
        connections.put(session, session);
        gameLobbies.put(session, gameID);
    }

    public void remove(Session session) {
        connections.remove(session);
        gameLobbies.remove(session);
    }

    public void broadcast(Session excludeSession, int gameID, ServerMessage notification) throws IOException {
        String msg = new Gson().toJson(notification);
        for (Session c : connections.values()) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession) && gameLobbies.get(c) == gameID) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }

    public void dm(Session targetSession, ServerMessage notification) throws IOException {
        String msg = new Gson().toJson(notification);
        targetSession.getRemote().sendString(msg);
    }
}
