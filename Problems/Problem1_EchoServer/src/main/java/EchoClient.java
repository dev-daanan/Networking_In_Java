package Problems.Problem1_EchoServer.src.main.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * The EchoClient class represents a simple client application that connects to an echo server.
 *
 * <p>Upon starting, the client will connect to the server using the specified hostname and port.
 * The client reads lines of text from the console and sends them to the server. The server responds
 * by echoing the sent text back to the client, which the client then displays.
 * <p>
 * The client will continue to read and send lines of text until terminated or an end-of-stream signal is received.
 */
public class EchoClient {

    /**
     * The main method for the EchoClient application.
     *
     * <p>This method sets up a socket connection to a specified server and port. After the connection
     * is established, the client reads lines of text from the console, sends them to the server,
     * and then displays the echoed response from the server.
     *
     * @param args Not used.
     * @throws IOException if an I/O error occurs when creating the socket connection, or when sending
     *                     or receiving data.
     */
    public static void main(String[] args) throws IOException {
        // The hostname (IP Address) of the server to connect to.
        String hostName = "localhost";

        // The port number on which the client will connect to the server. Must match the server's listening port.
        int portNumber = 5555;

        try (
                // Socket connection to the server.
                Socket echoSocket = new Socket(hostName, portNumber);

                // PrintWriter to send data to the server.
                PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);

                // BufferedReader to read data from the server.
                BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));

                // BufferedReader to read user input from the console.
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            String userInput;
            System.out.println("Message from server: " + in.readLine());

            // Read user input from the console and send to the server until an end-of-stream signal is received.
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                System.out.println("Echoed from server: " + in.readLine());
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }
    }

}

