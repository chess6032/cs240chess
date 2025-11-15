package dataaccess;

import chess.model.UserData;
import dataaccess.exceptions.SqlException;
import dataaccess.sqldao.SqlUserDAO;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

public class UserDAOTests {
    private final UserDAO dao;

    private static final UserData defaultUser = new UserData("username", "password", "email");
    private static final UserData mario = new UserData("mario", "peachlover44", "supermario@mariobrosplumbing.org");
    private static final UserData luigi = new UserData("luigi", "mariobros3", "luigi@mariobrosplumbing.org");
    private static final UserData peach = new UserData("peach", "bowserIsHot!", "princesspeach@mkindgom.org");

    private static final UserData[] users = new UserData[]{defaultUser, mario, luigi, peach};

    {
        try {
            dao = new SqlUserDAO();
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }
    }

    // UTILITY

    void fillTable() throws SqlException {
        for (var user : users) {
            dao.createUser(user.username(), user.password(), user.email());
        }
    }

    // TESTS

    @BeforeEach
    void clear() {
        try {
            dao.clear();
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testClear() {
        try {
            dao.clear();
            Assertions.assertEquals(0, dao.size());
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testCreateUserPositive() {
        try {
            int i = 0;
            for (var user : users) {
                Assertions.assertTrue(dao.createUser(user.username(), user.password(), user.email()));
                Assertions.assertEquals(++i, dao.size());
            }
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testCreateUserAlreadyExists() {
        try {
            // fill table with users
            fillTable();
            int size = dao.size();

            // make sure adding pre-existing users fails
            for (var user : users) {
                Assertions.assertFalse(dao.createUser(user.username(), user.password(), user.email()));
                Assertions.assertEquals(size, dao.size());
            }
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetUserPositive() {
        try {
            fillTable();
            for (var user : users) {
                Assertions.assertNotNull(dao.getUser(user.username()));
            }
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetNonExistentUser() {
        try {
            dao.clear();
            for (var user : users) {
                Assertions.assertNull(dao.getUser(user.username()));
            }
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testPasswordPositive() {
        try {
            fillTable();
            for (var user : users) {
                Assertions.assertTrue(dao.passwordMatches(user.username(), user.password()));
            }
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testPasswordDoesNotMatch() {
        try {
            fillTable();
            for (var user : users) {
                Assertions.assertFalse(dao.passwordMatches(user.username(), user.password() + " "));
            }
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }
    }

}
