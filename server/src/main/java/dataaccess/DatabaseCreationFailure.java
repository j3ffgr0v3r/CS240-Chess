package dataaccess;

public class DatabaseCreationFailure extends DataAccessException {

    public DatabaseCreationFailure() {
        super("Server Error: failed to create database");
    }

    public DatabaseCreationFailure(Throwable ex) {
        super("Server Error: failed to create database", ex);
    }
    
}
