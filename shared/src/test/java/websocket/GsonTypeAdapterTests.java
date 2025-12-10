package websocket;

import com.google.gson.Gson;
import model.GameData;
import websocket.commands.*;
import websocket.messages.ErrorServerMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import static websocket.commands.UserGameCommand.buildUserGameCommandGson;
import static websocket.messages.ServerMessage.buildServerMessageGson;

import org.junit.jupiter.api.*;

public class GsonTypeAdapterTests {
    @DisplayName("Deserialize UserGameCommand")
    @Test
    public void testUserGameCommandDeserialization() {
        // test UserGameCommand subclass deserialization
        var specialGson = buildUserGameCommandGson();

        var mmCommand = new MakeMoveCommand("auth2", 69, new chess.ChessMove(new chess.ChessPosition(1, 1), new chess.ChessPosition(8, 8), null));
        var cCommand = new ConnectCommand("auth3", 420);
        System.out.println(mmCommand);
        System.out.println(cCommand);

        System.out.println();

        System.out.println(specialGson.fromJson(new Gson().toJson(mmCommand), UserGameCommand.class));
        System.out.println(specialGson.fromJson(new Gson().toJson(cCommand), UserGameCommand.class));
    }

    @DisplayName("Serialize ServerMessage")
    @Test
    public void testServerMessageSubclassSerialization() {
        // test ServerMessage subclass serialization
        var specialGson = buildServerMessageGson();

        ServerMessage err = new ErrorServerMessage("Tragedy");
        System.out.println(err);
        System.out.println(specialGson.toJson(err));

        System.out.println();

        ServerMessage load = new LoadGameMessage(new GameData(69, null, null, null, new chess.ChessGame()));
        System.out.println(load);
        System.out.println(specialGson.toJson(load));
    }

    @DisplayName("Deserialize ServerMessage")
    @Test
    public void testServerMessageDeserialization() {
        var specialGson = buildServerMessageGson();

        ServerMessage err = new ErrorServerMessage(null);
        ServerMessage load = new LoadGameMessage(new GameData(-67, null, null, null, null));
        ServerMessage notif = new NotificationMessage(null, null);

        for (ServerMessage msg : new ServerMessage[]{err, load, notif}) {
            System.out.println(msg);
            System.out.println(new Gson().toJson(msg));

            ServerMessage deserialized = specialGson.fromJson(new Gson().toJson(msg), ServerMessage.class);
            System.out.println(deserialized);

            Assertions.assertEquals(msg, deserialized);
            System.out.println();
        }
    }

    @DisplayName("Serialize UserGameCommand")
    @Test
    public void testUserGameCommandSerialization() {
        var specialGson = buildUserGameCommandGson();

        UserGameCommand con = new ConnectCommand(null, -1);
        UserGameCommand move = new MakeMoveCommand(null, -2, null);
        UserGameCommand res = new ResignCommand(null, -3);
        UserGameCommand leave = new LeaveCommand(null, -4);

        for (UserGameCommand command : new UserGameCommand[]{con, move, res, leave}) {
            System.out.println(command);

            String serialized = specialGson.toJson(command);
            System.out.println(serialized);

            UserGameCommand deserialized = specialGson.fromJson(serialized, UserGameCommand.class);
            System.out.println(deserialized);

            Assertions.assertEquals(command, deserialized);
            System.out.println();
        }
    }
}
