package service;

import serverhandler.HTTPException;

public class ServiceException extends HTTPException {
    
    public ServiceException(int statusCode, String message) {
        super(statusCode, message);
    }

}
