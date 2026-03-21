package ui;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;

import client.ServerCommunicationFailure;
import client.ServerFacade;
import model.exceptions.AlreadyTakenException;
import model.exceptions.BadRequestException;

public class ChessClient {
    private final ServerFacade server;

    private String username = null;
    private enum UIState {
        PRELOGIN, POSTLOGIN, GAMEPLAY
    }
    UIState uiState = UIState.PRELOGIN;

    private record MenuOption(String usage, Consumer<String[]> func) {
    }

    private final List<MenuOption> preLoginMenu = List.of(
            new MenuOption("register <USERNAME> <PASSWORD> <EMAIL> - register a new account", (params) -> register(params[0], params[1], params[2])),
            new MenuOption("login <USERNAME> <PASSWORD> - sign in to play", null),
            new MenuOption("quit - quit the program", null),
            new MenuOption("help - see more information about this menu", null));

    private final List<MenuOption> postLoginMenu = List.of(new MenuOption("create <NAME> - start a new game", null),
            new MenuOption("list - list all active games", null),
            new MenuOption("join <ID> [WHITE|BLACK] - join a game as selected team", null),
            new MenuOption("observe <ID> - spectate an active game", null),
            new MenuOption("logout - logout from the current user", null),
            new MenuOption("quit - quit the program", null),
            new MenuOption("help - see more information about this menu", null));

    private final List<MenuOption> gameplayMenu = List.of(new MenuOption("leave - leave this game", null));

    private final Map<UIState, List<MenuOption>> stateMenuOptions = Map.of(
            UIState.PRELOGIN, preLoginMenu,
            UIState.POSTLOGIN, postLoginMenu,
            UIState.GAMEPLAY, gameplayMenu);

    public ChessClient(final String serverUrl) throws ServerCommunicationFailure {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        final boolean running = true;
        try (Scanner scanner = new Scanner(System.in)) {
            while (running) {
                printMenu();
                final String line = scanner.nextLine();
                System.out.println();
                try {
                    String[] params = line.trim().split("\\s+");
                    getCommand(params).accept(params);
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                    System.out.println();
                }
            }
        }
    }

    private void printMenu() {
        for (final MenuOption option : stateMenuOptions.get(uiState)) {
            System.out.println(option.usage);
        }
        System.out.println();
    }

    private Consumer<String[]> getCommand(String... args) throws IllegalArgumentException {
        if (args.length <= 0 || args[0].isEmpty()) {
            return null;
        }

        MenuOption command = stateMenuOptions.get(uiState).stream().filter(option -> option.usage().split("\\s+")[0].equals(args[0])).findFirst().orElse(null);

        if (command == null) {
            throw new IllegalArgumentException("Error: Unknown command '" + args[0] + "'.  Please select a valid command from the menu. Enter 'help' for more information.");
        }
        
        String[] commandFormat = command.usage().split("-")[0].split("\\s+");

        if (args.length < commandFormat.length) {
            throw new IllegalArgumentException("Error: Missing Arguments. Command argument format: " + command.usage().split("-")[0]);
        } else if (args.length > commandFormat.length) {
            throw new IllegalArgumentException("Error: Positional argument '" + args[commandFormat.length] + "' not recognized. Command argument format: " + command.usage().split("-")[0]);
        }

        return command.func();
    }

    private void register(String username, String email, String password) {
        try {
            this.username = server.register(username, email, password);
            uiState = UIState.POSTLOGIN;
        } catch (BadRequestException | AlreadyTakenException e) {
            System.out.println(e.getMessage());
        }
    }
}
