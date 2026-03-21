package model.requests;

public record SessionCreationRequest(String username, String password) implements Request {

}
