package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PieceMovesCalculator {

    public record Behavior(int dir_row, int dir_col, int distance) {}

    public static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor color, List<Behavior> behaviors) {
        List<ChessMove> moves = new ArrayList<>();


        for(Behavior behavior: behaviors) {
            for (int i = 1; behavior.distance == -1 || i < behavior.distance + 1; i++) {
                int newRow = position.getRow() + behavior.dir_row * i;
                int newColumn = position.getColumn() + behavior.dir_col * i;

                if(newRow < 1 || newRow > 8 || newColumn < 1 || newColumn > 8) {
                    break;
                }

                ChessPosition newPosition = new ChessPosition(newRow, newColumn);
                ChessPiece tile = board.getPiece(newPosition);

                if (tile == null || tile.getTeamColor() != color) {
                    moves.add(new ChessMove(position, newPosition, null));
                    if (tile != null && tile.getTeamColor() != color) {
                        break;
                    }
                } else {
                    break;
                }
            }
        }

        return moves;
    }

}
