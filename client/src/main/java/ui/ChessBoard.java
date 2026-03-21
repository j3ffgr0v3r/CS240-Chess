package ui;

import java.util.Iterator;
import chess.ChessGame;
import chess.ChessPiece;
import static ui.EscapeSequences.*;

public final class ChessBoard {
    private chess.ChessBoard board;

    public ChessBoard(chess.ChessBoard board) {
        updateGame(board);
    }

    public void updateGame(chess.ChessBoard board) {
        this.board = board;
    }

    @Override
    public String toString() {
        String output = "";
        for (ChessPiece[] row : board.toArray()) {
            output += "|";
            for (ChessPiece space : row) {
                output += pieceToChar(space) + "|";
            }
            output += "\n";
        }
        return output;
    }

    private String pieceToChar(ChessPiece space) {
        if (space == null) {
            return EMPTY;
        }
        switch (space.getPieceType()) {
            case KING -> {
                return (space.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_KING : BLACK_KING;
            }
            case QUEEN -> {
                return (space.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_QUEEN : BLACK_QUEEN;
            }
            case BISHOP -> {
                return (space.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_BISHOP : BLACK_BISHOP;
            }
            case ROOK -> {
                return (space.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_ROOK : BLACK_ROOK;
            }
            case KNIGHT -> {
                return (space.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_KNIGHT : BLACK_KNIGHT;
            }
            case PAWN -> {
                return (space.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_PAWN : BLACK_PAWN;
            }
            default -> {
                return "?";
            }
        }

    }
}
