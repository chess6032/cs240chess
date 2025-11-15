package dataaccess;

import dataaccess.exceptions.SqlException;
import dataaccess.sqldao.SqlUserDAO;
import org.junit.jupiter.api.*;

public class UserDAOTests extends DAOTests {
    private final UserDAO dao;

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
    @DisplayName("clear")
    void testClear() {
        try {
            dao.clear();
            Assertions.assertEquals(0, dao.size());
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("create user")
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
    @DisplayName("create user - username taken")
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
    @DisplayName("get user")
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
    @DisplayName("get user - empty")
    void testGetUserOnEmpty() {
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
    @DisplayName("passwords match")
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
    @DisplayName("passwords don't match")
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
