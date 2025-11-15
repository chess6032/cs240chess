package dataaccess;

import chess.model.UserData;

public class DAOTests {

    protected static final UserData DEFAULT_USER = new UserData("username", "password", "email");
    protected static final UserData MARIO = new UserData("mario", "peachlover44", "supermario@mariobrosplumbing.org");
    protected static final UserData LUIGI = new UserData("luigi", "mariobros3", "luigi@mariobrosplumbing.org");
    protected static final UserData PEACH = new UserData("peach", "bowserIsHot!", "princesspeach@mkindgom.org");

    protected static final UserData[] USERS = new UserData[]{DEFAULT_USER, MARIO, LUIGI, PEACH};
}
