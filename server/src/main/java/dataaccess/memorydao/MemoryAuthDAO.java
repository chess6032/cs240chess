package dataaccess.memorydao;

import chess.model.AuthData;
import dataaccess.AuthDAO;

import java.util.HashSet;

public class MemoryAuthDAO implements AuthDAO {

    private final HashSet<AuthData> authDatas = new HashSet<>();

    @Override
    public void clear() {
        authDatas.clear();
    }
}
