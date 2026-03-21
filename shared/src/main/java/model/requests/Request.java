package model.requests;

public sealed interface Request permits RegisterRequest, SessionCreationRequest, JoinGameRequest, CreateGameRequest {

}
