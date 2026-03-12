package dataaccess.user;

import org.junit.jupiter.api.BeforeEach;

import dataaccess.DataAccessException;

class MemoryUserDAOTest extends UserDAOTest {

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = new MemoryUserDAO();
    }
}