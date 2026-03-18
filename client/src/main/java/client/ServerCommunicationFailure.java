package client;

public class ServerCommunicationFailure extends Exception {

    public ServerCommunicationFailure() {
    }

    public ServerCommunicationFailure(String message) {
        super(message);
    }

    public ServerCommunicationFailure(String message, Throwable cause) {
        super(message, cause);
    }
    
}
