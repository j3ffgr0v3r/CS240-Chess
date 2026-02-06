package chess;

import java.util.Arrays;
import java.util.Iterator;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter signature of the
 * existing methods.
 */
public class ChessBoard implements Iterable<ChessPiece> {

    private ChessPiece[][] board = new ChessPiece[8][8];
    private final ChessPiece[][] defaultBoard
            = {
                {new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK)},
                {new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN)},
                new ChessPiece[8],
                new ChessPiece[8],
                new ChessPiece[8],
                new ChessPiece[8],
                {new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN)},
                {new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK)}
            };

    public ChessBoard() {
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board (How the game of chess
     * normally starts)
     */
    public void resetBoard() {
        board = defaultBoard;
    }

    @Override
    public String toString() {
        String output = "";
        for (ChessPiece[] row : board) {
            output += "|";
            for (ChessPiece space : row) {
                if (space == null) {
                    output += " ";
                } else {
                    switch (space.getPieceType()) {
                        case KING -> {
                            output += (space.getTeamColor() == ChessGame.TeamColor.WHITE) ? "K" : "k";
                        }
                        case QUEEN -> {
                            output += (space.getTeamColor() == ChessGame.TeamColor.WHITE) ? "Q" : "q";
                        }
                        case BISHOP -> {
                            output += (space.getTeamColor() == ChessGame.TeamColor.WHITE) ? "B" : "b";
                        }
                        case ROOK -> {
                            output += (space.getTeamColor() == ChessGame.TeamColor.WHITE) ? "R" : "r";
                        } 
                        case KNIGHT -> {
                            output += (space.getTeamColor() == ChessGame.TeamColor.WHITE) ? "N" : "n";
                        }
                        case PAWN -> {
                            output += (space.getTeamColor() == ChessGame.TeamColor.WHITE) ? "P" : "p";
                        }
                        default -> {
                        }
                    }
                }
                output += "|";
                }
            output += "\n";
            }
        return output;
    }

    @Override
    public Iterator<ChessPiece> iterator() {
        return new Iterator<ChessPiece>() {
            // Start stepping through the array from the beginning
            private int next = 0;
            private int row = 0;
            private int col = 0;

            @Override
            public boolean hasNext() {
                // Check if a current element is the last in the array
                return (next <= board.length*board[0].length - 1);
            }

            @Override
            public ChessPiece next() {
                // Get the value to be returned
                ChessPiece retValue = board[row][col];

                // Increment the counter in preparation for the next call
                next += 1;
                row = next / 8;
                col = next % 8;

                return retValue;
            }
        };
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() == obj.getClass()) {
            int x = 1;
            for (ChessPiece[] row : board) {
                int y = 1;
                for (ChessPiece space : row) {
                    if (!((space == null && ((ChessBoard) obj).getPiece(new ChessPosition(x, y)) == null) || (space != null && space.equals(((ChessBoard) obj).getPiece(new ChessPosition(x, y)))))) {
                        return false;
                    }
                    y += 1;
                }
                x += 1;
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

}
