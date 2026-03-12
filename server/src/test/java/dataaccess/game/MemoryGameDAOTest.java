package dataaccess.game;

import org.junit.jupiter.api.BeforeEach;

import dataaccess.DataAccessException;

class MemoryGameDAOTest extends GameDAOTest {

    @BeforeEach
    public void setUp() throws DataAccessException {
        gameDAO = new MemoryGameDAO();
    }
}