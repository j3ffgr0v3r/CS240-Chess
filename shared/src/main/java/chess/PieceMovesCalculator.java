package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PieceMovesCalculator {

    public record Behavior(int dir_row, int dir_col, int distance) {}

    public static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position, ArrayList<Behavior> behaviors) {
        List<ChessMove> moves = new ArrayList<>();

        for(Behavior behavior: behaviors) {
            int newRow = position.getRow() + behavior.dir_row;
            int newColumn = position.getColumn() + behavior.dir_col;
            ChessPosition newPosition = new ChessPosition(newRow, newColumn);
            ChessPiece tile = board.getPiece(newPosition);

            if (tile != null) {
                moves.add(new ChessMove(position, newPosition, null));
            }
        }

        return moves;
    }

}
