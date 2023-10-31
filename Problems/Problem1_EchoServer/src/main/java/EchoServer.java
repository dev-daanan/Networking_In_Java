package Problems.Problem1_EchoServer.src.main.java;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;


public class EchoServer {
    private static final int PORT_NUMBER = 5555;
    private static final int MAX_MESSAGE_LENGTH = 100_000;
    private static final int SOCKET_TIMEOUT = 10_000;
    public static void main(String[] args) {


        try (
                ServerSocket serverSocket = new ServerSocket(EchoServer.PORT_NUMBER);

                Socket clientSocket = serverSocket.accept();

                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

                DataInputStream in = new DataInputStream(clientSocket.getInputStream())
        ) {
            System.out.println("Server started and connected to client through: " +
                    "\nHostAddress: " + serverSocket.getInetAddress().getCanonicalHostName() +
                    "\nPortNumber: " + EchoServer.PORT_NUMBER);

            while (true) {
                int length = in.readInt();
                if (length < 0 || length > 100_000) {
                    System.out.println("Invalid message length received");
                    return;
                }
                byte[] bytes = new byte[length];
                in.readFully(bytes);

                String receivedMessage = new String(bytes, StandardCharsets.UTF_8);
                System.out.println("Received from client: " + receivedMessage);

                out.writeInt(length);
                out.write(bytes);
            }
        } catch (
                IOException e) {
            System.out.println("Exception caught when trying to listen on port " + EchoServer.PORT_NUMBER
                    + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}

