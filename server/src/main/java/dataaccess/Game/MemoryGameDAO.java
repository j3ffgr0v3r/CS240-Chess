package dataaccess.Game;

import java.util.Map;

import chess.ChessGame;

public class MemoryGameDAO implements GameDAO {

    Map<String, ChessGame> games;

    @Override
    public void clear() {
        games.clear();        
    }
    
}
