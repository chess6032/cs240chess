package service;

import dataaccess.GameDAO;

public record GameService(GameDAO gameDAO) {
    public void clear() {
        gameDAO.clear();
    }
}
