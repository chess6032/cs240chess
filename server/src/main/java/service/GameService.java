package service;

import dataaccess.GameDAO;

public interface GameService {
    static void clearGames(GameDAO gameDAO) {
        gameDAO.clearGames();
    }
}
