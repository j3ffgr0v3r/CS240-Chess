package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import chess.PieceMovesCalculator.Behavior;
import static chess.PieceMovesCalculator.calculateMoves;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter signature of the
 * existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING, QUEEN, BISHOP, KNIGHT, ROOK, PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    public void promotePiece(PieceType type) {
        this.type = type;
    }

    /**
     * Calculates all the positions a chess piece can move to Does not take into
     * account moves that are illegal due to leaving the king in danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);

        List<Behavior> behaviors = new ArrayList<>();

        if (null != piece.getPieceType()) {
            switch (piece.getPieceType()) {
                case KING -> {
                    behaviors.add(new Behavior(1, 1, 1));
                    behaviors.add(new Behavior(-1, 1, 1));
                    behaviors.add(new Behavior(1, -1, 1));
                    behaviors.add(new Behavior(-1, -1, 1));
                    behaviors.add(new Behavior(1, 0, 1));
                    behaviors.add(new Behavior(-1, 0, 1));
                    behaviors.add(new Behavior(0, 1, 1));
                    behaviors.add(new Behavior(0, -1, 1));
                }
                case QUEEN -> {
                    behaviors.add(new Behavior(1, 1, -1));
                    behaviors.add(new Behavior(-1, 1, -1));
                    behaviors.add(new Behavior(1, -1, -1));
                    behaviors.add(new Behavior(-1, -1, -1));
                    behaviors.add(new Behavior(1, 0, -1));
                    behaviors.add(new Behavior(-1, 0, -1));
                    behaviors.add(new Behavior(0, 1, -1));
                    behaviors.add(new Behavior(0, -1, -1));
                }
                case BISHOP -> {
                    behaviors.add(new Behavior(1, 1, -1));
                    behaviors.add(new Behavior(-1, 1, -1));
                    behaviors.add(new Behavior(1, -1, -1));
                    behaviors.add(new Behavior(-1, -1, -1));
                }
                case ROOK -> {
                    behaviors.add(new Behavior(1, 0, -1));
                    behaviors.add(new Behavior(-1, 0, -1));
                    behaviors.add(new Behavior(0, 1, -1));
                    behaviors.add(new Behavior(0, -1, -1));
                }
                case KNIGHT -> {
                    behaviors.add(new Behavior(1, 2, 1));
                    behaviors.add(new Behavior(-1, 2, 1));
                    behaviors.add(new Behavior(1, -2, 1));
                    behaviors.add(new Behavior(-1, -2, 1));
                    behaviors.add(new Behavior(2, 1, 1));
                    behaviors.add(new Behavior(-2, 1, 1));
                    behaviors.add(new Behavior(2, -1, 1));
                    behaviors.add(new Behavior(-2, -1, 1));
                }
                case PAWN -> {
                    behaviors.addAll(pawnBehavior(board, myPosition));
                }
                default -> {
                }
            }
        }

        return calculateMoves(board, myPosition, piece.getTeamColor(), behaviors);
    }

    private List<Behavior> pawnBehavior(ChessBoard board, ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        List<Behavior> behaviors = new ArrayList<>();

        // White goes up, black goes down (this sounds very racist...)
        int direction = (pieceColor == ChessGame.TeamColor.WHITE) ? 1 : -1;

        // First move
        if ((pieceColor == ChessGame.TeamColor.WHITE && row == 2)
                || (pieceColor == ChessGame.TeamColor.BLACK && row == 7)) {
            behaviors.add(new Behavior(direction, 0, 2));
        } else {
            // Basic move
            behaviors.add(new Behavior(direction, 0, 1));
        }

        // Attack
        int scopeRow = row + direction;
        int scopeCol;
        for (int side : Arrays.asList(-1, 1)) {
            scopeCol = col + side;
            if (scopeRow >= 1 && scopeRow <= 8 && scopeCol >= 1 && scopeCol <= 8) {
                ChessPiece target = board.getPiece(new ChessPosition(scopeRow, scopeCol));
                if (target != null && target.getTeamColor() != pieceColor) {
                    behaviors.add(new Behavior(direction, side, 1));
                }
            }
        }

        return behaviors;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && getClass() == obj.getClass()
                && pieceColor == ((ChessPiece) obj).getTeamColor()
                && type == ((ChessPiece) obj).getPieceType();
    }

    @Override
    public int hashCode() {
        return pieceColor.hashCode() * type.hashCode();
    }

}
