package requestsandresults.joingame;

public record JoinGameRequest(String authToken, String playerColor, int gameID) {

}
