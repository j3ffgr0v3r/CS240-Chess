package model.exceptions;

public class UnauthorizedException extends ServiceException {

    public UnauthorizedException() {
        super(401, "Error: unauthorized");
    }
}
