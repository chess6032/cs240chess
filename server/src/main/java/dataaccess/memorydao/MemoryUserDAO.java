package dataaccess.memorydao;

import chess.model.UserData;
import dataaccess.UserDAO;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {

    private final HashMap<String, UserData> users = new HashMap<>();

    @Override
    public void clear() {
        users.clear();
    }
}
