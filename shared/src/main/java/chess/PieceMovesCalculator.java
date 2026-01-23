package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PieceMovesCalculator {

    public record Behavior(int dir_row, int dir_col, int distance) {}

    public static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor color, List<Behavior> behaviors) {
        List<ChessMove> moves = new ArrayList<>();

        List<ChessPiece.PieceType> promotions = new ArrayList<>(Arrays.asList(
                    ChessPiece.PieceType.QUEEN,
                    ChessPiece.PieceType.BISHOP,
                    ChessPiece.PieceType.KNIGHT,
                    ChessPiece.PieceType.ROOK
        ));


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
                    // Split Behavior (Pawn)
                    if (board.getPiece(position).getPieceType() == ChessPiece.PieceType.PAWN ) {
                        if ((position.getColumn() == newColumn && tile == null) ||
                            (position.getColumn() != newColumn && tile != null)) {

                            // Promotion
                            if(newRow == 1 || newRow == 8) {
                                for (ChessPiece.PieceType promotion : promotions) {
                                    moves.add(new ChessMove(position, newPosition, promotion));
                                }
                            } else {
                                moves.add(new ChessMove(position, newPosition, null));
                            }
                        }

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

}
