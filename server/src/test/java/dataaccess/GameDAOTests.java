package dataaccess;

import chess.model.GameData;
import dataaccess.exceptions.SqlException;
import dataaccess.sqldao.SqlGameDAO;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.HashMap;

public class GameDAOTests extends DAOTests {

    private final GameDAO dao;

    {
        try {
            dao = new SqlGameDAO();
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void clear() {
        try {
            dao.clear();
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("create game")
    public void testCreateGamePositive() {
        try {
            dao.createGame("game");
            Assertions.assertEquals(1, dao.size());
            dao.createGame("game1");
            Assertions.assertEquals(2, dao.size());
            dao.createGame("game");
            Assertions.assertEquals(3, dao.size());
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("create game - null name")
    public void testCreateGameNullName() {
        try {
            Assertions.assertEquals(-1, dao.createGame(null));
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("add player")
    public void testAddPlayerPositive() {
        try {
            int id = dao.createGame("name");
            Assertions.assertTrue(dao.addPlayerToGame(id, defaultUser.username(), "WHITE"));
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("add player - color taken")
    public void testAddPlayerColorTaken() {
        try {
            int id = dao.createGame("name");
            Assertions.assertTrue(dao.addPlayerToGame(id, mario.username(), "WHITE"));
            Assertions.assertFalse(dao.addPlayerToGame(id, luigi.username(), "WHITE"));
            Assertions.assertTrue(dao.addPlayerToGame(id, luigi.username(), "BLACK"));
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("add player - empty")
    public void testAddPlayerOnEmpty() {
        try {
            Assertions.assertFalse(dao.addPlayerToGame(-1, defaultUser.username(), "WHITE"));
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("add player - bad color input")
    public void testAddPlayerBadColor() {
        try {
            int id = dao.createGame("name");
            Assertions.assertFalse(dao.addPlayerToGame(id, defaultUser.username(), "SKIBIDI"));
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("get game")
    public void getGamePositive() {
        try {
            int id = dao.createGame("name");
            GameData game = dao.getGame(id);
            Assertions.assertEquals(id, game.gameID());
            Assertions.assertEquals("name", game.gameName());
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("get game - empty")
    public void getGameOnEmpty() {
        try {
            Assertions.assertNull(dao.getGame(-1));
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("get all games")
    public void getAllGamesPositive() {
        var names = new String[]{"a", "b", "c", "d", "e"};
        var nameToID = new HashMap<String, Integer>();
        try {
            for (var name : names) {
                nameToID.put(
                        name,
                        dao.createGame(name)
                );
            }

            var allGames = dao.getAllGames();
            for (var name : names) {
                boolean nameFound = false;
                for (var game : allGames) {
                    if (game.gameName().equals(name)) {
                        nameFound = true;
                        break;
                    }
                }
                Assertions.assertTrue(nameFound);
            }
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("get all games - empty")
    public void getAllGamesOnEmpty() {
        try {
            Assertions.assertEquals(0, dao.getAllGames().size());
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }
    }
}
