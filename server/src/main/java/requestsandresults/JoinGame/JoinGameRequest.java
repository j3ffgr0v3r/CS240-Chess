package requestsandresults.JoinGame;

public record JoinGameRequest(String authToken, String playerColor, int gameID) {

}
