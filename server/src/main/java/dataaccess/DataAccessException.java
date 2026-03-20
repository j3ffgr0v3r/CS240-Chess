package dataaccess;

import model.exceptions.HTTPException;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends HTTPException {

    public DataAccessException(String message) {
        super(500, message);
    }

    public DataAccessException(String message, Throwable ex) {
        super(500, message, ex);
    }
}
