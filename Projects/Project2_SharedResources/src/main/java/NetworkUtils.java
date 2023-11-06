package Projects.Project2_SharedResources.src.main.java;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class NetworkUtils {
    /**
     * Reads a message from a client.
     *
     * @param in the input stream from the client's socket
     * @return the message read from the client, or null if the end of the stream has been reached
     * @throws IOException if an I/O error occurs while reading from the stream
     */
    public static String readMessage(DataInputStream in) throws IOException {
        // Read the length of the incoming message
        int length = in.readInt();

        // If length is negative, there is no message to read
        if (length < 0) {
            return null;
        }

        // Read the message bytes
        byte[] messageBytes = new byte[length];
        in.readFully(messageBytes);

        // Convert the byte array to a String and return
        return new String(messageBytes, StandardCharsets.UTF_8);
    }

    /**
     * Sends a message to the client.
     *
     * @param out the output stream to the client's socket
     * @param message the message to be sent
     * @throws IOException if an I/O error occurs while writing to the stream
     */
    public static void sendMessage(DataOutputStream out, String message) throws IOException {
        // Convert the message to a byte array
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);

        // Write the length of the message
        out.writeInt(messageBytes.length);

        // Write the message
        out.write(messageBytes);
    }
}

