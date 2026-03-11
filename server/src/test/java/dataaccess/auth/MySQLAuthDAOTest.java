package dataaccess.auth;

import org.junit.jupiter.api.BeforeEach;

import dataaccess.DataAccessException;

class MySQLAuthDAOTest extends AuthDAOTest {

    @BeforeEach
    public void setUp() throws DataAccessException {
        authDAO = new MySQLAuthDAO();
        authDAO.clear();
    }
}
