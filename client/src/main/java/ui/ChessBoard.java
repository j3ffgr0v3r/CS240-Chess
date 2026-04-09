package ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import static ui.EscapeSequences.BLACK_BISHOP;
import static ui.EscapeSequences.BLACK_KING;
import static ui.EscapeSequences.BLACK_KNIGHT;
import static ui.EscapeSequences.BLACK_PAWN;
import static ui.EscapeSequences.BLACK_QUEEN;
import static ui.EscapeSequences.BLACK_ROOK;
import static ui.EscapeSequences.EMPTY;
import static ui.EscapeSequences.RESET_BG_COLOR;
import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_BG_COLOR_BLUE;
import static ui.EscapeSequences.SET_BG_COLOR_DARK_GREEN;
import static ui.EscapeSequences.SET_BG_COLOR_DARK_GREY;
import static ui.EscapeSequences.SET_BG_COLOR_GREEN;
import static ui.EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
import static ui.EscapeSequences.SET_BG_COLOR_YELLOW;
import static ui.EscapeSequences.WHITE_BISHOP;
import static ui.EscapeSequences.WHITE_KING;
import static ui.EscapeSequences.WHITE_KNIGHT;
import static ui.EscapeSequences.WHITE_PAWN;
import static ui.EscapeSequences.WHITE_QUEEN;
import static ui.EscapeSequences.WHITE_ROOK;

public final class ChessBoard {
    private chess.ChessGame game;
    ChessGame.TeamColor side;

    private final String borderBGColor = SET_BG_COLOR_BLUE;
    private final String[] header = { borderBGColor + EMPTY, " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ", EMPTY + RESET_BG_COLOR };

    public ChessBoard(chess.ChessGame game, ChessGame.TeamColor side) {
        updateGame(game);
        this.side = side;
    }

    public void updateGame(chess.ChessGame update) {
        this.game = update;
    }

    @Override
    public String toString() {
        return toString(false, null);
    }

    public String toString(boolean highlight, ChessPosition pos) {
        Iterator<ChessPiece> pieces = game.getBoard().iterator();
        String[][] boardDisplay = new String[10][10];

        List<ChessPosition> highlightedSquares = new ArrayList<>();

        if (highlight) {
            highlightedSquares = game.validMoves(pos).stream().map(move -> move.getEndPosition()).collect(Collectors.toCollection(ArrayList::new));
        }

        boardDisplay[0] = header.clone();
        boardDisplay[9] = header.clone();

        for (int row = 1; row < 9; row++) {
            boardDisplay[row][0] = borderBGColor + " %s ".formatted(String.valueOf(row));
            for (int col = 1; col < 9; col++) {
                String bgColor = (row + col) % 2 == 0 ? SET_BG_COLOR_DARK_GREY : SET_BG_COLOR_LIGHT_GREY;
                if (highlight) {
                    if (highlightedSquares.contains(new ChessPosition(row, col))) {
                        bgColor = bgColor.equals(SET_BG_COLOR_DARK_GREY) ? SET_BG_COLOR_DARK_GREEN : SET_BG_COLOR_GREEN;
                    } else if (new ChessPosition(row, col).equals(pos)) {
                        bgColor = SET_BG_COLOR_YELLOW;
                    }
                }
                boardDisplay[row][col] = bgColor + "%-1s".formatted(pieceToChar(pieces.next()));
            }
            boardDisplay[row][9] = borderBGColor + " %s ".formatted(String.valueOf(row)) + RESET_BG_COLOR;
        }

        if (side == ChessGame.TeamColor.WHITE) {
            Collections.reverse(Arrays.asList(boardDisplay));
        } else if (side == ChessGame.TeamColor.BLACK) {
            for (String[] row : boardDisplay) {
                Collections.reverse(Arrays.asList(row).subList(1, row.length - 1));
            }
        }

        return Arrays.stream(boardDisplay).map(row -> Arrays.stream(row).collect(Collectors.joining())).collect(Collectors.joining("\n"))
                + RESET_BG_COLOR + RESET_TEXT_COLOR;
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
