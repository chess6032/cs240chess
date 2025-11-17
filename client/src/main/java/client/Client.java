package client;

public class Client {
    private final ServerFacade server;

    public Client(String serverURL) {
        server = new ServerFacade(serverURL);
    }
}
