package service;

public class BadRequestException extends ServiceException {
    public BadRequestException() {
        super(400, "Error: bad request");
    }
}
