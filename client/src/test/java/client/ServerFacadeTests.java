package client;

import dataaccess.exceptions.SqlException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import static server.HttpResponseCodes.*;
import server.Server;

import java.util.Collection;
import java.util.TreeSet;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    static int numUsers;

    private static UserData getNewUser() {
        return new UserData(Integer.toString(++numUsers), "password", "email");
    }

    @BeforeAll
    public static void init() {
        server = new Server();
        try {
            numUsers = server.getUserDAO().size()+1;
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void testRegisterPositive() {
        var user = getNewUser();

        try {
            serverFacade.register(user);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }

        AuthData auth;

        try {
            auth = serverFacade.login(user);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(user.username(), auth.username());
    }

    @Test
    public void testRegisterAlreadyTaken() {
        var user = getNewUser();

        try {
            serverFacade.register(user);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }

        ResponseException exc = null;

        try {
            serverFacade.register(user);
        } catch (ResponseException e) {
            exc = e;
        }

        Assertions.assertNotNull(exc);
        Assertions.assertEquals(ALREADY_TAKEN_STATUS, exc.getStatus());
    }

    @Test
    public void testLoginPositive() {
        testRegisterPositive();
    }

    @Test
    public void testLoginNotRegistered() {
        ResponseException exc = null;
        try {
            serverFacade.login(getNewUser());
        } catch (ResponseException e) {
            System.out.println("skibidi");
            exc = e;
        }

        Assertions.assertNotNull(exc);
        Assertions.assertEquals(UNAUTHORIZED_STATUS, exc.getStatus());
    }

    @Test
    public void testLogoutPositive() {
        var user = getNewUser();
        AuthData auth = null;
        try {
            auth = serverFacade.register(user);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }

        try {
            serverFacade.logout(auth);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }

        boolean excThrown = false;

        try {
            serverFacade.listGames(auth);
        } catch (ResponseException e) {
            excThrown = true;
        }

        Assertions.assertTrue(excThrown);
    }
}
