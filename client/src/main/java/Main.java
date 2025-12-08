import chess.*;
import client.Client;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        String serverURL = "http://localhost:8080";
        if (args.length > 0) {
            serverURL = args[0];
        }

//        System.out.println(Arrays.toString(args));

        new Client(serverURL, false).run();
    }
}