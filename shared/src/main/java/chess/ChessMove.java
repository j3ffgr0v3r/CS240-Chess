package chess;

import java.util.Objects;

import chess.ChessPiece.PieceType;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    @Override
    public String toString() {
        return String.format("%s -> %s", startPosition, endPosition);
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() == obj.getClass()) {
            if (this.getStartPosition().getRow() == ((ChessMove) obj).getStartPosition().getRow() && 
                this.getStartPosition().getColumn() == ((ChessMove) obj).getStartPosition().getColumn() &&
                this.getEndPosition().getRow() == ((ChessMove) obj).getEndPosition().getRow() && 
                this.getEndPosition().getColumn() == ((ChessMove) obj).getEndPosition().getColumn() &&
                this.getPromotionPiece() == ((ChessMove) obj).getPromotionPiece()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPosition, endPosition, promotionPiece);
    }

}
