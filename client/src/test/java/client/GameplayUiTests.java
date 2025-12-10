package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import model.NotificationInfo;
import org.junit.jupiter.api.*;
import ui.CommandAndArgs;
import ui.InvalidArgsFromUser;
import ui.phases.GameplayUI;
import ui.uidrawing.UIDrawer;
import websocket.messages.ErrorServerMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.NotificationMessage.NotificationType;

import static ui.uidrawing.UIDrawer.println;

public class GameplayUiTests {

    @DisplayName("static: update move")
    @Test
    public void testUpdateMovePrint() throws ResponseException, InvalidArgsFromUser {

//       GameplayUI.parsePromotionInput(null);
        // FIXME: ^ parsePromotionInput isn't currently being used, which pisses off the autograder,
        //  but I'll want it for later so I'm just going to throw it here for the time being.

        // test updateMove
        var board = new chess.ChessBoard();
            board.resetBoard();
            board.removePiece(new ChessPosition(7, 1));
            board.removePiece(new ChessPosition(8, 1));
            board.addPiece(new ChessPosition(7, 1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        var game = new chess.ChessGame();
            game.setBoard(board);

        var ui = new GameplayUI(null, new GameData(-1, null, null, null, game), ChessGame.TeamColor.WHITE);
    //        ui.drawBoard();
            ui.eval(new CommandAndArgs("highlight", new String[]{"a7"})).run();

        Runnable func = null;
            try {
            func = ui.eval(new CommandAndArgs("move", new String[]{"a7", "a8", "q"}));
        } catch (
        InvalidArgsFromUser e) {
            System.out.println(e.getMessage());

        }

        println(ui.getMove());
            if (func != null) { func.run(); }
    }

    @DisplayName("printing NotificationMessage")
    @Test
    public void testPrintNotif() {
        int i = 1;
        for (NotificationType notifType : NotificationType.values()) {
            UIDrawer.print(notifType);
            GameplayUI.evaluateWsNotifPrint(new NotificationMessage(notifType,
                    new NotificationInfo("Bobby" + i++, ChessGame.TeamColor.WHITE, new ChessMove(new ChessPosition(1, 1),
                            new ChessPosition(1, 3), ChessPiece.PieceType.QUEEN))))
                .run();
            UIDrawer.println();
        }
    }

    @DisplayName("printing ErrorMessage")
    @Test
    public void testPrintError() {
        var messages = new String[]{
                "erm, what the sigma??",
                "Error: Error: Error: Error:",
                "",
                null,
                "skibidi gyatt"
        };
        for (String msg : messages) {
            GameplayUI.printWsError(new ErrorServerMessage(msg));
        }
    }
}
