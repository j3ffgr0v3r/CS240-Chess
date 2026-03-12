package dataaccess.user;

import org.junit.jupiter.api.BeforeEach;

import dataaccess.DataAccessException;

class MySQLUserDAOTest extends UserDAOTest {

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = new MySQLUserDAO();
        userDAO.clear();
    }
}
