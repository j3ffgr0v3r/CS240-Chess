package dataaccess.Game;

import java.util.HashMap;
import java.util.Map;

import chess.ChessGame;

public class MemoryGameDAO implements GameDAO {

    Map<String, ChessGame> games;

    public MemoryGameDAO() {
        games = new HashMap<>();
    }

    @Override
    public void clear() {
        games.clear();
    }

}
