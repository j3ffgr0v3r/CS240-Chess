package client;

import model.exceptions.HTTPException;

public class NotAcceptableException extends HTTPException {
    
    public NotAcceptableException() {
        super(406, "Error: bad request");
    }

}
