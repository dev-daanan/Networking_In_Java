package Problems.Problem1_EchoServer.src.main.java;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;


public class EchoClient {


    public static void main(String[] args) {
        String hostName = "localhost";

        int portNumber = 5555;

        try (
                Socket echoSocket = new Socket(hostName, portNumber);

                DataOutputStream out = new DataOutputStream(echoSocket.getOutputStream());

                DataInputStream in = new DataInputStream(echoSocket.getInputStream());

                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            System.out.println("Server started and connected to client through: " +
                    "\nHostAddress: " + hostName +
                    "\nPortNumber: " + portNumber);
            while (true) {
                String userInput = stdIn.readLine();
                if (userInput.isEmpty()) {
                    continue;
                }

                byte[] bytes = userInput.getBytes(StandardCharsets.UTF_8);
                out.writeInt(bytes.length);
                out.write(bytes);

                int length = in.readInt();

                if (length < 0 || length > 100_000) {
                    System.out.println("Invalid message length received");
                    return;
                }

                byte[] responseBytes = new byte[length];
                in.readFully(responseBytes);
                String echoedMessage = new String(responseBytes, StandardCharsets.UTF_8);

                System.out.println("Received from server: " + echoedMessage);
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

