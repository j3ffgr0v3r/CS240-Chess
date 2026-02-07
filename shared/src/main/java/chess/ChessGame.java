package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame implements Cloneable {

    private TeamColor teamTurn;
    private ChessBoard board;

    

    private final ChessMove whiteCastleKingside = new ChessMove(new ChessPosition(1, 5), new ChessPosition(1, 3), null);
    private final ChessMove whiteCastleQueenside = new ChessMove(new ChessPosition(1, 5), new ChessPosition(1, 7), null);
    private final ChessMove blackCastleKingside = new ChessMove(new ChessPosition(8, 5), new ChessPosition(8, 7), null);
    private final ChessMove blackCastleQueenside = new ChessMove(new ChessPosition(8, 5), new ChessPosition(8, 3), null);

    private final List<ChessMove> castleMoves = new ArrayList<>(Arrays.asList(whiteCastleKingside, whiteCastleQueenside, blackCastleKingside, blackCastleQueenside));
    private final List<Boolean> canCastleList = new ArrayList<>(Arrays.asList(true, true, true, true));

    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        for (int i = 0; i < 4; i++) {
            canCastleList.set(i, true);
        }
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

        Collection<ChessMove> potentialMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> output = new ArrayList<>(potentialMoves);

        
        for(ChessMove move : potentialMoves) {
            if (putsIntoCheck(piece.getTeamColor(), move)) {
                output.remove(move);
            }
        }

        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            output.addAll(castlingMoves(startPosition));
        }

        return output;
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
        // Respond to castling by moving rook
        if (castleMoves.contains(move)) {
            ChessPosition endPosition = move.getEndPosition();
            int rookDirection = (endPosition.getColumn() - startPosition.getColumn())/2;
            ChessPosition rook = new ChessPosition(endPosition.getRow(), (rookDirection == 1)?8:1);
            ChessMove rookResponse = new ChessMove(rook, new ChessPosition(endPosition.getRow(), endPosition.getColumn() + ((rookDirection == 1)?-1:1)), null);
            board.movePiece(rookResponse);
        }
        updateCastling(move);

        // Promotion
        if (move.getPromotionPiece() != null) {
            piece.promotePiece(move.getPromotionPiece());
        }

        teamTurn = (teamTurn == TeamColor.WHITE)?TeamColor.BLACK:TeamColor.WHITE;
    }

    /**
     * Returns additional castling moves for the piece at the given location, if applicable
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid castling moves for requested piece, or null if none
     */
    public Collection<ChessMove> castlingMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null || piece.getPieceType() != ChessPiece.PieceType.KING) {
            return null;
        }

        Collection<ChessMove> output = new ArrayList<>();
        TeamColor color = piece.getTeamColor();

        int direction;
        int side;
        ChessMove castleMove;
        for(int i = 0; i < 4 ;i++){
            castleMove = castleMoves.get(i);
            side = (i < 2)?1:8;
            direction = (i % 2 == 0)?-1:1;

            if (canCastleList.get(i) && board.getPiece(new ChessPosition(side, startPosition.getColumn()+direction)) == null && 
                                    board.getPiece(new ChessPosition(side, startPosition.getColumn()+(direction*2))) == null &&
                                    !putsIntoCheck(color, new ChessMove(startPosition, new ChessPosition(side, startPosition.getColumn()+direction), null)) &&
                                    !putsIntoCheck(color, new ChessMove(startPosition, new ChessPosition(side, startPosition.getColumn()+(direction*2)), null))) {
                output.add(castleMove);
            }
        }

        return output;
    }

    /**
     * Checks if player has moved King or Rook from starting position, and if so, flags player ineligible for castling
     *
     * @param teamColor which team to check for eligibility to castle
     * @return True if the specified team can castle
     */
    public void updateCastling(ChessMove move) {
        ChessPiece piece = board.getPiece(move.getEndPosition());
        TeamColor color = piece.getTeamColor();

        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (color == TeamColor.WHITE) {
                canCastleList.set(0, false);
                canCastleList.set(1, false);
            } else {
                canCastleList.set(2, false);
                canCastleList.set(3, false);
            }
        } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            int side = move.getStartPosition().getColumn();
            if (color == TeamColor.WHITE) {
                if (side == 1) {
                    canCastleList.set(1, false);
                } else if (side == 8){
                    canCastleList.set(0, false);
                }
            } else {
                if (side == 1) {
                    canCastleList.set(3, false);
                } else if (side == 8){
                    canCastleList.set(2, false);
                }
            }
        }
    }

    /**
     * Determines if a chess move would put the given teams king into check
     *
     * @param teamColor which team to check for hypothetical check
     * @return True if the specified team would be in check
     */
    public boolean putsIntoCheck(TeamColor teamColor, ChessMove move) {
        ChessGame hypotheticalChessGame;
        
        hypotheticalChessGame = this.clone();
        hypotheticalChessGame.board.movePiece(move);

        return hypotheticalChessGame.isInCheck(teamColor);
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
            // King not found... somehow. So I guess they're... not in check???
            return false;
        }

        // See if any piece can "kill" him
        pos = 0;
        for(ChessPiece piece : board) {
            if(piece != null && piece.getTeamColor() != teamColor) {
                for (ChessMove move : piece.pieceMoves(board, new ChessPosition(pos/8+1, pos%8+1))) {
                    if (move.getEndPosition().equals(kingPosition)) {
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
            if(piece != null && piece.getTeamColor() == teamColor && !validMoves(new ChessPosition(pos/8+1, pos%8+1)).isEmpty()) {
                return true;
            }
            pos ++;
        }
        return false;
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
