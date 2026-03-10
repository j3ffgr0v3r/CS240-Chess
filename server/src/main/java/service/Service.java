package service;

import dataaccess.DataAccessException;
import dataaccess.auth.AuthDAO;

public abstract class Service {

    AuthDAO authDAO;

    public Service(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    protected void isAuthorized(String authToken) throws UnauthorizedException, DataAccessException {
        if (authDAO.getSession(authToken) == null) {
            throw new UnauthorizedException();
        }
    }
}
