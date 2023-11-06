package Projects.Project2_SharedResources.src.main.java;

import java.io.*;
import java.net.Socket;

public class Client {

    private static final String SERVER_HOSTNAME = "localhost";
    private static final int SERVER_PORT = 5555;

    public static void main(String[] args) {
        System.out.println("Connecting to the resource server (" + SERVER_HOSTNAME + ":" + SERVER_PORT + ")");

        // Connect to the server
        try (Socket socket = new Socket(SERVER_HOSTNAME, SERVER_PORT)) {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));


            while (true) {
                // Read options
                System.out.println(NetworkUtils.readMessage(in));
                // Get users choice
                String userChoice = stdIn.readLine();
                // Send users choice
                NetworkUtils.sendMessage(out, userChoice);
                // Read list or get prompt
                System.out.println(NetworkUtils.readMessage(in));
                // if only reading list end here
                if (userChoice.equals("1")) {
                    continue;
                }
                // if getting prompt keep going
                // Send answer to prompt
                NetworkUtils.sendMessage(out, stdIn.readLine());
            }
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }

        System.out.println("Client exited.");
    }
}

