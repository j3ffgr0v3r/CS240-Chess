package service;

public class BadRequestException extends ServiceException {
    public BadRequestException() {
        super(401, "Error: bad request");
    }
}
