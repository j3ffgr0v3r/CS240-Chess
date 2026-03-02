package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PieceMovesCalculator {

    public record Behavior(int dirRow, int dirCol, int distance) {

    }

    public static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor color,
            List<Behavior> behaviors) {

        List<ChessMove> moves = new ArrayList<>();

        for (Behavior behavior : behaviors) {
            for (int i = 1; behavior.distance == -1 || i < behavior.distance + 1; i++) {
                int newRow = position.getRow() + behavior.dirRow * i;
                int newColumn = position.getColumn() + behavior.dirCol * i;

                if (newRow < 1 || newRow > 8 || newColumn < 1 || newColumn > 8) {
                    break;
                }

                ChessPosition newPosition = new ChessPosition(newRow, newColumn);
                ChessPiece tile = board.getPiece(newPosition);

                if (tile == null || tile.getTeamColor() != color) { 
                    if (board.getPiece(position).getPieceType() == ChessPiece.PieceType.PAWN) {
                        // Split Behavior (Pawn)
                        moves.addAll(pawnMoves(position, newPosition, tile));

                    } else {
                        // Attack and move together
                        moves.add(new ChessMove(position, newPosition, null));
                    }
                    // Attacking is last move
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

    private static Collection<ChessMove> pawnMoves(ChessPosition position, ChessPosition newPosition, ChessPiece tile) {
        List<ChessPiece.PieceType> promotions = new ArrayList<>(Arrays.asList(
                ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.ROOK));

        int newRow = newPosition.getRow();
        int newColumn = newPosition.getColumn();

        List<ChessMove> moves = new ArrayList<>();

        if ((position.getColumn() == newColumn && tile == null)
                || (position.getColumn() != newColumn && tile != null)) {

            // Promotion
            if (newRow == 1 || newRow == 8) {
                for (ChessPiece.PieceType promotion : promotions) {
                    moves.add(new ChessMove(position, newPosition, promotion));
                }
            } else {
                moves.add(new ChessMove(position, newPosition, null));
            }
        }

        return moves;
    }

}
