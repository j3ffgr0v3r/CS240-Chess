package serverhandler;

public class HTTPException extends Exception {

    private final int statusCode;

    public HTTPException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public HTTPException(int statusCode, String message, Throwable ex) {
        super(message, ex);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

}
