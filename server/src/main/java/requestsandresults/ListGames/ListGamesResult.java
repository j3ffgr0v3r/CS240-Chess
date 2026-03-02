package requestsandresults.ListGames;

import java.util.List;

import model.GameData;

public record ListGamesResult(String message, List<GameData> games) {
    
}
