package ui;

import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;

import chess.ChessGame;
import chess.ChessPosition;
import client.NotificationHandler;
import client.ServerCommunicationFailure;
import client.ServerFacade;
import model.GameData;
import model.exceptions.HTTPException;
import static ui.EscapeSequences.ERASE_SCREEN;

public class ChessClient {
    private final ServerFacade server;
    private ChessBoard board;
    private NotificationHandler notificationHandler;

    private String username = null;
    private final List<Integer> gameIDs = new ArrayList<>();

    private enum UIState {
        QUIT, PRELOGIN, POSTLOGIN, GAMEPLAY
    }

    UIState uiState = UIState.PRELOGIN;

    private record MenuOption(String usage, String description, Consumer<String[]> func) {
    }

    private final List<MenuOption> preLoginMenu = List.of(
            new MenuOption("register <USERNAME> <EMAIL> <PASSWORD> - register a new account",
                    "If this is your first time playing, use this command to register an account!",
                    (params) -> register(params[1], params[2], params[3])),
            new MenuOption("login <USERNAME> <PASSWORD> - sign in to play",
                    "Use this command to sign back in to an account you have made to get back to playing chess.",
                    (params) -> login(params[1], params[2])),
            new MenuOption("quit - quit the program", "If you're done playing chess, use this command to close this program.", (params) -> quit()),
            new MenuOption("help - see more information about this menu", "Use this command to see these command descriptions.", (params) -> help()));

    private final List<MenuOption> postLoginMenu = List.of(
            new MenuOption("create <NAME> - start a new game", "Begin a new round of chess with the given name to play against an opponent.",
                    (params) -> createGame(params[1])),
            new MenuOption("list - list all active games", "See what games are available to play or watch, and who is signed up to play them.",
                    (params) -> listGames()),
            new MenuOption("join {ID} [WHITE|BLACK] - join a game as selected team",
                    "Join the selected chess game under the given play color. Note that the selected team must be available.",
                    (params) -> joinGame(Integer.parseInt(params[1]), params[2])),
            new MenuOption("observe {ID} - spectate an active game", "Watch the selected chess game without playing.",
                    (params) -> observeGame(Integer.parseInt(params[1]))),
            new MenuOption("logout - logout from the current user", "Sign out of your account and return to the login menu.", (params) -> logout()),
            new MenuOption("quit - quit the program", "If you're done playing chess, use this command to close this program.", (params) -> quit()),
            new MenuOption("help - see more information about this menu", "Use this command to see these command descriptions.", (params) -> help()));

    private final List<MenuOption> gameplayMenu = List.of(
            new MenuOption("help - see more information about this menu", "Use this command to see these command descriptions.", (params) -> help()),
            new MenuOption("redraw - manually redraw the chess board", "Use this command to manually refresh the view of the chess board.",
                    (params) -> redraw()),
            new MenuOption("leave - leave this game", "Stop participating in this game and return to the game selection menu.",
                    (params) -> leaveGame()),
            new MenuOption("move (from) (to) - move a chess piece", "Use this command to move one of your chess pieces on your turn.",
                    (params) -> move(params[1], params[2])),
            new MenuOption("resign - give up", "Use this command to surrender this game to your opponent.", (params) -> resign()),
            new MenuOption("highlight (position) - show legal moves for given piece",
                    "Use this command to see the available moves for the piece at the given location.", (params) -> highlight(params[1])));

    private final Map<UIState, List<MenuOption>> stateMenuOptions = Map.of(
            UIState.PRELOGIN, preLoginMenu,
            UIState.POSTLOGIN, postLoginMenu,
            UIState.GAMEPLAY, gameplayMenu);

    public ChessClient(final String serverUrl) throws ServerCommunicationFailure {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (uiState != UIState.QUIT) {
                if (uiState == UIState.GAMEPLAY) {
                    printGame();
                }
                printMenu();
                final String line = scanner.nextLine();
                System.out.println();
                System.out.print(ERASE_SCREEN);
                try {
                    String[] params = line.trim().split("\\s+");
                    Consumer<String[]> command = getCommand(params);
                    if (command != null) {
                        command.accept(params);
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                    System.out.println();
                }
            }
        } catch (IllegalStateException ex) {
            System.err.println("Error: websocket connection lost...");
            uiState = UIState.POSTLOGIN;
        }
    }

    private void printMenu() {
        for (final MenuOption option : stateMenuOptions.get(uiState)) {
            System.out.println(option.usage);
        }
        System.out.println();
    }

    private void printGame() {
        String[] gameBoard = board.toString().split("\n");
        for (int i = 0; i < 10; i++) {
            System.out.print(gameBoard[i]);
            if (i >= 1 && i <= 8) {
                System.out.print(" ".repeat(10));
                System.out.print(notificationHandler.getNotification(abs(i-8)));
            }
            System.out.println();
        }
    }

    private Consumer<String[]> getCommand(String... args) throws IllegalArgumentException {
        if (args.length <= 0 || args[0].isEmpty()) {
            return null;
        }

        MenuOption command = stateMenuOptions.get(uiState).stream().filter(option -> option.usage().split("\\s+")[0].equals(args[0])).findFirst()
                .orElse(null);

        if (command == null) {
            throw new IllegalArgumentException(
                    "Error: Unknown command '" + args[0] + "'.  Please select a valid command from the menu. Enter 'help' for more information.");
        }

        String[] commandFormat = command.usage().split("-")[0].split("\\s+");

        if (args.length < commandFormat.length) {
            throw new IllegalArgumentException("Error: Missing Arguments. Command argument format: " + command.usage().split("-")[0]);
        } else if (args.length > commandFormat.length) {
            throw new IllegalArgumentException(String.format("Error: Positional argument '%s' not recognized. Command argument format: %s",
                    args[commandFormat.length], command.usage().split("-")[0]));
        }

        int counter = 0;
        for (final String arg : commandFormat) {
            if (arg.charAt(0) == '{') {
                try {
                    Integer.valueOf(args[counter]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(String.format("Error: Positional argument '%s' is not valid. Command argument format: %s",
                            args[counter], command.usage().split("-")[0]));
                }
            } else if (arg.charAt(0) == '['
                    && !Arrays.asList(arg.substring(1, arg.length() - 1).split("\\|")).contains(args[counter].toUpperCase())) {
                throw new IllegalArgumentException(String.format("Error: Positional argument '%s' is not valid. Command argument format: %s",
                        args[counter], command.usage().split("-")[0]));
            } else if (arg.charAt(0) == '(') {
                int digitCount = 0;
                int charAGCount = 0;
                boolean illegalChar = false;

                for (char c : args[counter].toCharArray()) {
                    if (Character.isDigit(c) && Character.getNumericValue(c) >= 1 && Character.getNumericValue(c) <= 8) {
                        digitCount++;
                    } else if (c >= 'a' && c <= 'g') {
                        charAGCount++;
                    } else {
                        illegalChar = true;
                    }
                    // Early exit if more than one is found
                    if (digitCount > 1 || charAGCount > 1 || illegalChar) {
                        break;
                    }
                }
                if (digitCount != 1 || charAGCount != 1 || illegalChar) {
                    throw new IllegalArgumentException(String.format("Error: Positional argument '%s' is not valid. Command argument format: %s",
                            args[counter], command.usage().split("-")[0]));
                }
            }
            counter++;
        }

        return command.func();
    }

    private void quit() {
        System.out.println("Goodbye!");
        uiState = UIState.QUIT;
    }

    private void help() {
        System.out.println("""
                Welcome to Chess! Below are the possible commands you can enter to begin playing, followed by a brief description of what they do. \
                Simply enter a command keyword, followed by the acceptable parameters.\n Parameters surrounded by <> accept any input, \
                {} take only numbers, [] accept only the options listed within, and () take chess board coordinates (such as 'a8').\n""");
        for (final MenuOption option : stateMenuOptions.get(uiState)) {
            System.out.println(option.usage.split("\\s+")[0] + " - " + option.description);
        }
        System.out.println();
    }

    private void register(String username, String email, String password) {
        try {
            this.username = server.register(username, email, password);
            uiState = UIState.POSTLOGIN;
        } catch (HTTPException e) {
            System.out.println(e.getMessage());
        }
    }

    private void login(String username, String password) {
        try {
            this.username = server.login(username, password);
            uiState = UIState.POSTLOGIN;
        } catch (HTTPException e) {
            System.out.println(e.getMessage());
        }
    }

    private void logout() {
        try {
            server.logout();
            this.username = null;
            uiState = UIState.PRELOGIN;
        } catch (HTTPException e) {
            System.out.println(e.getMessage());
        }
    }

    private void createGame(String gameName) {
        try {
            int gameID = server.createGame(gameName);
            gameIDs.add(gameID);
            System.out.println(String.format("Game %s successfully started as game %d!\n", gameName, gameIDs.size()));
        } catch (HTTPException e) {
            System.out.println(e.getMessage());
        }
    }

    private void listGames() {
        try {
            List<GameData> games = server.listGames();
            gameIDs.clear();
            int counter = 0;
            for (final GameData game : games) {
                gameIDs.add(game.gameID());
                System.out.println(String.format("Game %d: %s - White Player: %s - Black Player %s", ++counter, game.gameName(),
                        game.whiteUsername() == null ? "--NONE--" : game.whiteUsername(),
                        game.blackUsername() == null ? "--NONE--" : game.blackUsername()));
            }
            System.out.println();
        } catch (HTTPException e) {
            System.out.println(e.getMessage());
        }
    }

    private void joinGame(int gameNumber, String team) {
        gameNumber -= 1;
        if (0 <= gameNumber && gameNumber < gameIDs.size()) {
            try {
                chess.ChessGame gameBoard = new chess.ChessGame();
                gameBoard.getBoard().resetBoard();
                board = new ChessBoard(gameBoard, "BLACK".equals(team.toUpperCase()) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE);
                notificationHandler = new NotificationHandler(board);
                server.joinGame(gameIDs.get(gameNumber), team.toUpperCase(), notificationHandler);
                uiState = UIState.GAMEPLAY;
            } catch (HTTPException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Error: Invalid game number " + (gameNumber + 1) + ". Enter 'list' to see available games.");
        }
    }

    private void observeGame(int gameNumber) {
        gameNumber -= 1;
        if (0 <= gameNumber && gameNumber < gameIDs.size()) {
            try {
                chess.ChessGame gameBoard = new chess.ChessGame();
                gameBoard.getBoard().resetBoard();
                board = new ChessBoard(gameBoard, ChessGame.TeamColor.WHITE);
                server.observeGame(gameIDs.get(gameNumber), notificationHandler);
                uiState = UIState.GAMEPLAY;
            } catch (HTTPException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Error: Invalid game number " + (gameNumber + 1) + ". Enter 'list' to see available games.");
        }
    }

    private void leaveGame() {
        try {
            server.leaveGame();
            uiState = UIState.POSTLOGIN;
        } catch (HTTPException e) {
            System.out.println(e.getMessage());
        }
    }

    private void redraw() {

    }

    private void move(String from, String to) {
        try {
            server.move(stringToPos(from), stringToPos(to));
        } catch (HTTPException e) {
            System.out.println(e.getMessage());
        }
    }

    private void resign() {
        try {
            server.resign();
        } catch (HTTPException e) {
            System.out.println(e.getMessage());
        }
    }

    private void highlight(String piece) {

    }

    private ChessPosition stringToPos(String pos) {
        int column = 0;
        int row = 0;

        for (char c : pos.toCharArray()) {
            if (Character.isDigit(c)) {
                row = Character.getNumericValue(c);
            } else if (c >= 'a' && c <= 'g') {
                column = c - 'a' + 1;
            }
        }

        return new ChessPosition(row, column);
    }

}
