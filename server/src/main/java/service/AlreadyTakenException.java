package service;

public class AlreadyTakenException extends ServiceException {
    public AlreadyTakenException() {
        super(403, "Error: already taken");
    }
}
