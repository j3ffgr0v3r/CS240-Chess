package model.exceptions;

public class ServiceException extends HTTPException {
    
    public ServiceException(int statusCode, String message) {
        super(statusCode, message);
    }

}
