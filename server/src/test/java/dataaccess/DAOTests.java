package dataaccess;

import chess.model.UserData;

public class DAOTests {

    protected static final UserData defaultUser = new UserData("username", "password", "email");
    protected static final UserData mario = new UserData("mario", "peachlover44", "supermario@mariobrosplumbing.org");
    protected static final UserData luigi = new UserData("luigi", "mariobros3", "luigi@mariobrosplumbing.org");
    protected static final UserData peach = new UserData("peach", "bowserIsHot!", "princesspeach@mkindgom.org");

    protected static final UserData[] users = new UserData[]{defaultUser, mario, luigi, peach};
}
