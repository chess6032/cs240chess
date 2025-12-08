package server.handlers;

import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;

public class WsRequestHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    @Override
    public void handleConnect(@NotNull WsConnectContext wsConnectContext) throws Exception {
        System.out.println("Websocket connected.");
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception {
        // currently, this just echos the message it receives.
        System.out.println("Incoming message: " + ctx.message());
        ctx.send(ctx.message());
    }

    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) throws Exception {
        System.out.println("Websocket closed.");
    }
}
