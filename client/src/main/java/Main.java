import chess.*;
import client.Client;

public class Main {
    public static void main(String[] args) {
        String serverURL = "http://localhost:8080";
        if (args.length > 0) {
            serverURL = args[1];
        }

        new Client(serverURL).run();
    }
}