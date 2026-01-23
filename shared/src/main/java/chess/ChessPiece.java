package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import chess.PieceMovesCalculator.Behavior;
import static chess.PieceMovesCalculator.calculateMoves;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
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

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);

        List<Behavior> behaviors = new ArrayList<>();

        if (null != piece.getPieceType()) switch (piece.getPieceType()) {
            case KING -> {
                behaviors.add(new Behavior(1,1,1));
                behaviors.add(new Behavior(-1,1,1));
                behaviors.add(new Behavior(1,-1,1));
                behaviors.add(new Behavior(-1,-1,1));
                behaviors.add(new Behavior(1,0,1));
                behaviors.add(new Behavior(-1,0,1));
                behaviors.add(new Behavior(0,1,1));
                behaviors.add(new Behavior(0,-1,1));
            }
            case QUEEN -> {
                behaviors.add(new Behavior(1,1,-1));
                behaviors.add(new Behavior(-1,1,-1));
                behaviors.add(new Behavior(1,-1,-1));
                behaviors.add(new Behavior(-1,-1,-1));
                behaviors.add(new Behavior(1,0,-1));
                behaviors.add(new Behavior(-1,0,-1));
                behaviors.add(new Behavior(0,1,-1));
                behaviors.add(new Behavior(0,-1,-1));
            }
            case BISHOP -> {
                behaviors.add(new Behavior(1,1,-1));
                behaviors.add(new Behavior(-1,1,-1));
                behaviors.add(new Behavior(1,-1,-1));
                behaviors.add(new Behavior(-1,-1,-1));
            }
            case ROOK -> {
                behaviors.add(new Behavior(1,0,-1));
                behaviors.add(new Behavior(-1,0,-1));
                behaviors.add(new Behavior(0,1,-1));
                behaviors.add(new Behavior(0,-1,-1));
            } 
            case KNIGHT -> {
                behaviors.add(new Behavior(1,2,1));
                behaviors.add(new Behavior(-1,2,1));
                behaviors.add(new Behavior(1,-2,1));
                behaviors.add(new Behavior(-1,-2,1));
                behaviors.add(new Behavior(2,1,1));
                behaviors.add(new Behavior(-2,1,1));
                behaviors.add(new Behavior(2,-1,1));
                behaviors.add(new Behavior(-2,-1,1));
            }
            case PAWN -> {
                

            }
            default -> {
            }
        }

        return calculateMoves(board, myPosition, piece.getTeamColor(), behaviors);
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && getClass() == obj.getClass() && 
                pieceColor == ((ChessPiece) obj).getTeamColor() &&
                type == ((ChessPiece) obj).getPieceType();
    }

    @Override
    public int hashCode() {
        return pieceColor.hashCode() * type.hashCode();
    }

    
}
