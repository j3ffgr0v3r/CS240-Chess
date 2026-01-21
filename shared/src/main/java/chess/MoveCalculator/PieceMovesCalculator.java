package chess.MoveCalculator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

public class PieceMovesCalculator {

    public record Vector(int dir_row, int dir_col, int distance) {}

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position, Collection<Vector> vectors) {
        List<ChessMove> moves = new ArrayList<>();

        for(Vector vector: vectors) {
            int newRow = position.getRow() + vector.dir_row;
            int newColumn = position.getColumn() + vector.dir_col;
            ChessPosition newPosition = new ChessPosition(newRow, newColumn);
            ChessPiece tile = board.getPiece(newPosition);

            if (tile != null) {
                moves.add(new ChessMove(position, newPosition, null));
            }
        }

        return moves;
    }

}
