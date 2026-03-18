package ui;

import client.ServerCommunicationFailure;

public class ChessClient {
    private final ServerFacade server;
    
    public ChessClient(String serverUrl) throws ServerCommunicationFailure {
        server = new ServerFacade(serverUrl);
    }

    public void run() {

    }
}
