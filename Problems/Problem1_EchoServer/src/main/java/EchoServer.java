package Problems.Problem1_EchoServer.src.main.java;

import java.io.*;
import java.net.*;

/**
 * The EchoServer class represents a simple server application that listens on a specified port.
 * When a client connects, anything the client sends to the server is echoed back to the client.
 * <p>
 * The server will continue to run and listen for incoming client connections until terminated.
 */
public class EchoServer {

    /**
     * The main method for the EchoServer application.
     *
     * <p>This method sets up a server socket that listens on a specified port. When a client
     * connects, it sets up input and output streams to communicate with the client. The server
     * then reads lines of text from the client and sends them back (echoes them) to the client.
     *
     * @param args Not used.
     * @throws IOException if an I/O error occurs when waiting for a connection or when handling
     *         the client input/output.
     */
    public static void main(String[] args) throws IOException {
        // The port number on which the server will listen for client connections.
        int portNumber = 5555;

        try (
                // ServerSocket that listens for client connection requests.
                ServerSocket serverSocket = new ServerSocket(portNumber);

                // Socket representing the client connection.
                Socket clientSocket = serverSocket.accept();

                // PrintWriter to send data to the client.
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                // BufferedReader to read data from the client.
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            String inputLine;
            out.println("You have successfully connected to the server socket");
            System.out.println("Successfully connected to: "+ clientSocket.getLocalAddress().getCanonicalHostName());

            // Read data from the client until the client closes the connection or sends an end-of-stream signal.
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received from client: " + inputLine);
                out.println(inputLine); // Echo the received data back to the client.
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port " + portNumber
                    + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}

