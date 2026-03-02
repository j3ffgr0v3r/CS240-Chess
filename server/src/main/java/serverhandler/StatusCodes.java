package serverhandler;

public enum StatusCodes {
    OK(200, "OK"), 
    BAD_REQUEST(400, "Error: bad request"),
    UNAUTHORIZED(401, "Error: unauthorized"),
    ALREADY_TAKEN(403, "Error: already taken");

    private final int code; 
    private final String message; 

    StatusCodes(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
