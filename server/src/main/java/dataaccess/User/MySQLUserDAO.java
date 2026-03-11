package dataaccess.user;

import com.google.gson.Gson;

import dataaccess.DataAccessException;
import dataaccess.MySQLDAO;
import model.UserData;

public class MySQLUserDAO extends MySQLDAO implements UserDAO {

    private final String[] createStatements = {
            """
                        CREATE TABLE IF NOT EXISTS  users (
                            `username` varchar(256) NOT NULL,
                            `userData` TEXT DEFAULT NULL,
                            PRIMARY KEY (`username`),
                            INDEX(username)
                        )
                    """
    };

    public MySQLUserDAO() throws DataAccessException {
        configureDatabase(createStatements);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String output = executeQuery(rs -> {
            return rs.getString("userData");
        }, "SELECT userData FROM users WHERE username = ?;", username);
        return new Gson().fromJson(output, UserData.class);
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        executeUpdate("INSERT INTO users (username, userData) VALUES (?, ?);", userData.username(), userData);
    }

    @Override
    public void clear() throws DataAccessException {
        executeUpdate("TRUNCATE TABLE users;");
    }

}
