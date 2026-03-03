package service;

import dataaccess.auth.AuthDAO;

public abstract class Service {

    AuthDAO authDAO;

    public Service(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    protected void isAuthorized(String authToken) throws UnauthorizedException {
        if (authDAO.getSession(authToken) == null) {
            throw new UnauthorizedException();
        }
    }
}
