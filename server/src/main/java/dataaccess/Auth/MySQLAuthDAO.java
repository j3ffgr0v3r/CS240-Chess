package dataaccess.auth;

import dataaccess.DataAccessException;
import dataaccess.MySQLDAO;
import model.AuthData;

public class MySQLAuthDAO extends MySQLDAO implements AuthDAO {

    private final String[] createStatements = {
            """
                        CREATE TABLE IF NOT EXISTS  auth (
                            `authToken` varchar(256) NOT NULL,
                            `authData` TEXT DEFAULT NULL,
                            PRIMARY KEY (`authToken`),
                            INDEX(authToken)
                        )
                    """
    };

    public MySQLAuthDAO() throws DataAccessException {
        configureDatabase(createStatements);
    }

    @Override
    public void createSession(AuthData authData) throws DataAccessException {
        executeUpdate("insert into auth (authToken, authData) VALUES (?, ?);", authData.authToken(), authData);
    }

    @Override
    public AuthData getSession(String authData) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void terminateSession(String authData) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
