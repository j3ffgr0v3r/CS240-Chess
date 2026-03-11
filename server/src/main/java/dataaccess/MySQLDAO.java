package dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static java.sql.Statement.RETURN_GENERATED_KEYS;

import com.google.gson.Gson;

public abstract class MySQLDAO {

    protected int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p)
                        ps.setString(i + 1, p);
                    else if (param instanceof Integer p)
                        ps.setInt(i + 1, p);
                    else
                        ps.setString(i + 1, new Gson().toJson(param));
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    @FunctionalInterface
    public interface SQLFunction<T> {
        T apply(ResultSet rs) throws SQLException;
    }

    public <T> T executeQuery(SQLFunction< T> mapper, String statement, Object... params) throws DataAccessException {
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
                        return mapper.apply(rs);
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
