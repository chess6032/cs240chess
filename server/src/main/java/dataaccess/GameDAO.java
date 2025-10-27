package dataaccess;

public interface GameDAO {
    void clear();
    int size();
    int createGame(String gameName);
}
