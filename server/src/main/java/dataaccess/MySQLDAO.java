package dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.gson.Gson;

public abstract class MySQLDAO {

    public ResultSet executeSQL(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p)
                        ps.setString(i + 1, p);
                    else if (param instanceof Integer p)
                        ps.setInt(i + 1, p);
                    else
                        ps.setString(i + 1, new Gson().toJson(param));
                }
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs;
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    protected void configureDatabase(String[] createStatements) throws DataAccessException, DatabaseConnectionFailure, DatabaseCreationFailure {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
