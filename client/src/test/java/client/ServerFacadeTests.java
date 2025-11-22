package client;

import dataaccess.exceptions.SqlException;
import model.AuthData;
import model.GameData;
import model.PlayerColorGameIDforJSON;
import model.UserData;
import org.junit.jupiter.api.*;
import static server.HttpResponseCodes.*;
import server.Server;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    static AuthData mainAuth;

    static int numUsers;

    private static UserData makeNewUser() {
        UserData user;
        try {
            do {
                user = new UserData(Integer.toString(numUsers++), "password", "email");
            } while (server.getUserDAO().getUser(user.username()) != null);
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    private static GameData createGame() {
        try {
            return serverFacade.createGame(mainAuth, "test");
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        serverFacade = new ServerFacade("http://localhost:" + port);


        try {
            numUsers = server.getUserDAO().size()+1;
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }

        var user = makeNewUser();
        try {
            mainAuth = serverFacade.register(user);
        } catch (ResponseException e) {
            System.out.println(user);
            throw new RuntimeException(e);
        }

        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void testRegisterPositive() {
        var user = makeNewUser();

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
        var user = makeNewUser();

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
            serverFacade.login(makeNewUser());
        } catch (ResponseException e) {
            System.out.println("skibidi");
            exc = e;
        }

        Assertions.assertNotNull(exc);
        Assertions.assertEquals(UNAUTHORIZED_STATUS, exc.getStatus());
    }

    @Test
    public void testLogoutPositive() {
        var user = makeNewUser();
        AuthData auth;
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

        ResponseException exc = null;

        try {
            serverFacade.listGames(auth);
        } catch (ResponseException e) {
            exc = e;
        }

        Assertions.assertNotNull(exc);
        Assertions.assertEquals(UNAUTHORIZED_STATUS, exc.getStatus());
    }

    @Test
    public void testLogoutNotRegistered() {

        ResponseException exc = null;

        try {
            serverFacade.logout(new AuthData("", ""));
        } catch (ResponseException e) {
            exc = e;
        }

        Assertions.assertNotNull(exc);
        Assertions.assertEquals(UNAUTHORIZED_STATUS, exc.getStatus());
    }

    @Test
    public void testCreateGamePositive() {
        try {
            serverFacade.createGame(mainAuth, "createGame");
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreateGameUnauthorized() {
        ResponseException exc = null;

        try {
            serverFacade.createGame(new AuthData("", ""), "this shouldn't exist");
        } catch (ResponseException e) {
            exc = e;
        }

        Assertions.assertNotNull(exc);
        Assertions.assertEquals(UNAUTHORIZED_STATUS, exc.getStatus());
    }

    @Test
    public void testListGamesPositive() {
        try {
            serverFacade.listGames(mainAuth);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testListGamesUnauthorized() {
        ResponseException exc = null;

        try {
            serverFacade.listGames(new AuthData("", ""));
        } catch (ResponseException e) {
            exc = e;
        }

        Assertions.assertNotNull(exc);
        Assertions.assertEquals(UNAUTHORIZED_STATUS, exc.getStatus());
    }

    @Test
    public void testJoinGamePositive() {
        GameData game = createGame();

        var white = makeNewUser();
        var black = makeNewUser();

        AuthData whiteAuth;
        AuthData blackAuth;

        try {
            whiteAuth = serverFacade.register(white);
            blackAuth = serverFacade.register(black);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }

        try {
            serverFacade.joinGame(whiteAuth, new PlayerColorGameIDforJSON("WHITE", game.gameID()));
            serverFacade.joinGame(blackAuth, new PlayerColorGameIDforJSON("BLACK", game.gameID()));
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testJoinGameAlreadyTaken() {

        int gameID = createGame().gameID();

        try {
            serverFacade.joinGame(mainAuth, new PlayerColorGameIDforJSON("WHITE", gameID));
            serverFacade.joinGame(mainAuth, new PlayerColorGameIDforJSON("BLACK", gameID));
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }

        ResponseException exc = null;
        try {
            serverFacade.joinGame(mainAuth, new PlayerColorGameIDforJSON("WHITE", gameID));
        } catch (ResponseException e) {
            exc = e;
        }
        Assertions.assertNotNull(exc);
        Assertions.assertEquals(ALREADY_TAKEN_STATUS, exc.getStatus());

        exc = null;
        try {
            serverFacade.joinGame(mainAuth, new PlayerColorGameIDforJSON("BLACK", gameID));
        } catch (ResponseException e) {
            exc = e;
        }
        Assertions.assertNotNull(exc);
        Assertions.assertEquals(ALREADY_TAKEN_STATUS, exc.getStatus());
    }
}
