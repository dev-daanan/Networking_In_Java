package Projects.Project1_ChatRoom.src.main.java;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class ChatRoomClientOne {
    public static void main(String[] args) {
        // Hostname of the server to connect to.
        String hostName = "localhost";
        // Port number on which the server is listening.
        int portNumber = 5555;

        try (
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
                Socket mySocket = new Socket(hostName, portNumber);
                DataInputStream in = new DataInputStream(mySocket.getInputStream());
                DataOutputStream out = new DataOutputStream(mySocket.getOutputStream())
        ) {
            System.out.println("Connected to \"" + hostName + "\" server on port " + portNumber);
            System.out.print("Enter your temporary username -> ");
            String userName = stdIn.readLine();

            // Sends username to server to be displayed
            String introMessage = userName + ", has entered the chat.";
            out.writeInt(introMessage.getBytes().length);
            out.write(introMessage.getBytes());

            // Receive the length of the chat history
            int length = in.readInt();
            // Receive the actual chat history
            byte[] chatBytes = new byte[length];
            in.readFully(chatBytes);
            String chat = new String(chatBytes, StandardCharsets.UTF_8);
            // Display the chat history.
            System.out.println(chat);

            while (true) {
                // Read user input.
                System.out.print("Enter Message -> ");
                String userInput = stdIn.readLine();
                // If user input is empty, skip the current loop iteration.
                if (userInput.isEmpty()) {
                    continue;
                }

                // Convert user input to bytes and send to server.
                byte[] bytes = ("[" + userName + "]: " + userInput).getBytes(StandardCharsets.UTF_8);
                out.writeInt(bytes.length);
                out.write(bytes);
            }
        } catch (UnknownHostException e) {
            // Handle errors related to unknown hosts.
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            // Handle generic I/O exceptions.
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }

    }

}
