package dataaccess;

public class DatabaseConnectionFailure extends DataAccessException {

    public DatabaseConnectionFailure() {
        super("Server Error: unable to connect to database");
    }

    public DatabaseConnectionFailure(Throwable ex) {
        super("Server Error: unable to connect to database", ex);
    }
    
}
