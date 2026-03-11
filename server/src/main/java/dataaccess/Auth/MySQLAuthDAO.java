package dataaccess.auth;

import java.sql.SQLException;

import com.google.gson.Gson;

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
        executeUpdate("INSERT INTO auth (authToken, authData) VALUES (?, ?);", authData.authToken(), authData);
    }

    @Override
    public AuthData getSession(String authToken) throws DataAccessException {
        try {
            return new Gson().fromJson(getFromTable("SELECT authData FROM auth WHERE authToken = ?;", authToken).getString("authData"), AuthData.class);
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void terminateSession(String authToken) throws DataAccessException {
        executeSQL("DELETE FROM auth WHERE authToken = ?;", authToken);
    }

    @Override
    public void clear() throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
