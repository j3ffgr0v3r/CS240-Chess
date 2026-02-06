package chess;

import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame implements Cloneable {

    private TeamColor teamTurn;
    private ChessBoard board;

    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null) {
            return null;
        }

        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);

        ChessGame hypotheticalChessGame;
        for(ChessMove move : moves) {
            hypotheticalChessGame = this.clone();
            hypotheticalChessGame.board.movePiece(move);

            if (hypotheticalChessGame.isInCheck(teamTurn)) {
                moves.remove(move);
            }
        }

        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null) {
            throw new InvalidMoveException("There is no piece there...");
        }
        if (piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("It is not your turn, cheater!");
        } 
        if (!validMoves(startPosition).contains(move)) {
            throw new InvalidMoveException("You can't move there, cheater!");
        } 

        board.movePiece(move);

        // Promotion
        if (move.getPromotionPiece() != null) {
            piece.promotePiece(move.getPromotionPiece());
        }

        teamTurn = (teamTurn == TeamColor.WHITE)?TeamColor.BLACK:TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = null;

        // Find King
        int pos = 0;
        for(ChessPiece piece : board) {
            if(piece != null && piece.getTeamColor() == teamColor && piece.getPieceType()==ChessPiece.PieceType.KING) {
                kingPosition = new ChessPosition(pos/8+1, pos%8+1);
                break;
            }
            pos ++;
        }
        if (kingPosition == null) {
            throw new RuntimeException("Attempted to find " + teamColor + " king, but could not.");
        }

        // See if any piece can "kill" him
        pos = 0;
        for(ChessPiece piece : board) {
            if(piece != null && piece.getTeamColor() != teamColor) {
                for (ChessMove move : piece.pieceMoves(board, new ChessPosition(pos/8+1, pos%8+1))) {
                    if (move.getEndPosition() == kingPosition) {
                        return true;
                    }
                }
            }
            pos ++;
        }
        return false;
    }


    /**
     * Determines if the given team has any valid moves
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team can make any valid move
     */
    public boolean hasValidMoves(TeamColor teamColor) {
        int pos = 0;
        
        for(ChessPiece piece : board) {
            if(piece != null && piece.getTeamColor() == teamColor && validMoves(new ChessPosition(pos/8, pos%8)) != null) {
                return false;
            }
            pos ++;
        }
        return true;
    }


    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && !hasValidMoves(teamColor);

    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && !hasValidMoves(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    @SuppressWarnings("CloneDeclaresCloneNotSupported")
    protected ChessGame clone() {
        try {
            ChessGame clone = (ChessGame) super.clone();
            clone.board = this.board.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && getClass() == obj.getClass() && 
                teamTurn == ((ChessGame) obj).getTeamTurn() &&
                ((ChessGame) obj).getBoard().equals(board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }

}
