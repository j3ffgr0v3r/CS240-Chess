package ui;

import chess.ChessGame;
import chess.ChessPiece;
import static ui.EscapeSequences.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Collectors;

public final class ChessBoard {
    private chess.ChessBoard board;
    ChessGame.TeamColor side;

    private final String borderBGColor = SET_BG_COLOR_BLUE;
    private final String[] header = {borderBGColor + EMPTY, " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ", EMPTY + RESET_BG_COLOR};

    public ChessBoard(chess.ChessBoard board, ChessGame.TeamColor side) {
        updateGame(board);
        this.side = side;
    }

    public void updateGame(chess.ChessBoard board) {
        this.board = board;
    }

    @Override
    public String toString() {
        Iterator<ChessPiece> pieces = board.iterator();
        String[][] boardDisplay = new String[10][10];

        boardDisplay[0] = header;
        boardDisplay[9] = header;        

        for (int row = 1; row < 9; row ++) {
            boardDisplay[row][0] = borderBGColor +  " %s ".formatted(String.valueOf(row));
            for (int col = 1; col < 9; col ++) {
                String bgColor = (row + col) % 2 == 0 ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREY;
                boardDisplay[row][col] = bgColor + "%-1s".formatted(pieceToChar(pieces.next()));
            }
            boardDisplay[row][9] = borderBGColor + " %s ".formatted(String.valueOf(row)) + RESET_BG_COLOR;
        }

        if (side == ChessGame.TeamColor.WHITE) {
            Collections.reverse(Arrays.asList(boardDisplay));
        }

        return Arrays.stream(boardDisplay).map(row -> Arrays.stream(row).collect(Collectors.joining())).collect(Collectors.joining("\n"))+RESET_BG_COLOR+RESET_TEXT_COLOR;
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
