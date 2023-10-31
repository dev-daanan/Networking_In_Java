package Problems.Problem1_EchoServer.src.main.java;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;


/**
 * Represents an echo client, which connects to a server and sends messages to it.
 * The server responds by echoing the messages back to the client.
 * The client then displays the echoed messages.
 */
public class EchoClient {

    /**
     * Entry point for the EchoClient application.
     *
     * @param args Command-line arguments (currently unused).
     */
    public static void main(String[] args) {
        // Hostname of the server to connect to.
        String hostName = "localhost";
        // Port number on which the server is listening.
        int portNumber = 5555;

        try (
                // Establish a socket connection to the server.
                Socket echoSocket = new Socket(hostName, portNumber);

                // Stream to send data to the server.
                DataOutputStream out = new DataOutputStream(echoSocket.getOutputStream());

                // Stream to receive data from the server.
                DataInputStream in = new DataInputStream(echoSocket.getInputStream());

                // Reader to get user input from the console.
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            // Display connection details.
            System.out.println("Server started and connected to client through: " +
                    "\nHostAddress: " + hostName +
                    "\nPortNumber: " + portNumber);

            while (true) {
                // Read user input.
                String userInput = stdIn.readLine();
                // If user input is empty, skip the current loop iteration.
                if (userInput.isEmpty()) {
                    continue;
                }

                // Convert user input to bytes and send to server.
                byte[] bytes = userInput.getBytes(StandardCharsets.UTF_8);
                out.writeInt(bytes.length);
                out.write(bytes);

                // Receive the echoed message length from the server.
                int length = in.readInt();
                // Validate the echoed message length.
                if (length < 0 || length > 100_000) {
                    System.out.println("Invalid message length received");
                    return;
                }

                // Receive the actual echoed message.
                byte[] responseBytes = new byte[length];
                in.readFully(responseBytes);
                String echoedMessage = new String(responseBytes, StandardCharsets.UTF_8);

                // Display the echoed message.
                System.out.println("Received from server: " + echoedMessage);
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


