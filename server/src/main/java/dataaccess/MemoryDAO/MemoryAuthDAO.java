package dataaccess.MemoryDAO;

import chess.model.AuthData;
import dataaccess.AuthDAO;

import java.util.HashSet;

public class MemoryAuthDAO implements AuthDAO {
    private final HashSet<AuthData> auths = new HashSet<>();

    @Override
    public void createAuth(AuthData authData) {
        auths.add(authData);
    }
}
