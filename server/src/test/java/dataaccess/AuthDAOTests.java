package dataaccess;

import dataaccess.exceptions.SqlException;
import dataaccess.sqldao.SqlAuthDAO;
import org.junit.jupiter.api.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class AuthDAOTests extends DAOTests {

    private final AuthDAO dao;

    {
        try {
            dao = new SqlAuthDAO();
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void clear() {
        try {
            dao.clear();
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("create auth - single")
    public void testCreateAuthSingle() {
        try {
            dao.createAuth(defaultUser.username());
            Assertions.assertEquals(1, dao.size());
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("create auth - multiple")
    public void testCreateAuthMultiple() {
        HashSet<String> auths = new HashSet<>();
        try {
            for (int i = 0; i < 5; ++i) {
                String auth = dao.createAuth(defaultUser.username());
                // make sure each auth is distinct
                Assertions.assertFalse(auths.contains(auth));
                auths.add(auth);

                Assertions.assertEquals(i+1, dao.size());
            }

        } catch (SqlException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("delete auth")
    public void testDeleteAuthPositive() {
        HashSet<String> auths = new HashSet<>();
        try {
            int i = 0;
            for (var user : users) {
                auths.add(dao.createAuth(user.username()));
                Assertions.assertEquals(++i, dao.size());
            }
            for (var auth : auths) {
                Assertions.assertTrue(dao.deleteAuth(auth));
                Assertions.assertEquals(--i, dao.size());
            }
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("delete auth - empty")
    public void testDeleteAuthOnEmpty() {
        try {
            Assertions.assertFalse(dao.deleteAuth(""));
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("find username correspond to auth")
    public void testFindUserOfAuthPositive() {
        HashMap<String, String> userToAuth = new HashMap<>();
        try {
            for (var user : users) {
                String username = user.username();
                userToAuth.put(username, dao.createAuth(username));
            }

            for (var user : users) {
                Assertions.assertEquals(
                        user.username(),
                        dao.findUserOfAuth(userToAuth.get(user.username()))
                );
            }

        } catch (SqlException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("find username correspond to auth - empty")
    public void testFindUserOfAuthOnEmpty() {
        try {
            Assertions.assertNull(dao.findUserOfAuth(""));
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }
    }
}
