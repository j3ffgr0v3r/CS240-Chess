package ui;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

import client.ServerCommunicationFailure;

public class ChessClient {
    private final ServerFacade server;
    private enum UIState {
        PRELOGIN,
        POSTLOGIN,
        GAMEPLAY
    }

    private record MenuOption(String usage, Function<Object, Object> func) {}
    private final List<MenuOption> preLoginMenu = List.of(new MenuOption("register <USERNAME> <PASSWORD> <EMAIL> - register a new account", null),
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

    private final Map<UIState, List<MenuOption>> stateMenuOptions = Map.of(UIState.PRELOGIN, preLoginMenu,
                                                                            UIState.POSTLOGIN, postLoginMenu,
                                                                            UIState.GAMEPLAY, gameplayMenu);
    
    public ChessClient(final String serverUrl) throws ServerCommunicationFailure {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        final boolean running = true;
        final UIState uiState = UIState.PRELOGIN;
        try (Scanner scanner = new Scanner(System.in)) {
            while (running) {
                printMenu(uiState);
                final String line = scanner.nextLine();
                System.out.println();
            }
        }
    }

    public void printMenu(final UIState state) {
        for (final MenuOption option : stateMenuOptions.get(state)) {
            System.out.println(option.usage);
        }
        System.out.println();
    }
}
