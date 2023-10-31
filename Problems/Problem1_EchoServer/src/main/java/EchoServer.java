package Problems.Problem1_EchoServer.src.main.java;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class EchoServer {
    /**
     * The port number on which the server listens.
     */
    private static final int PORT_NUMBER = 5555;
    /**
     * Maximum allowed message length in bytes.
     */
    private static final int MAX_MESSAGE_LENGTH = 100_000;
    /**
     * Socket timeout in milliseconds.
     */
    private static final int SOCKET_TIMEOUT = 60_000;

    /**
     * Entry point for the EchoServer application.
     * <p>
     * This method initializes the EchoServer, setting up a server socket that listens on a
     * predefined port. The server utilizes a ThreadPoolExecutor to handle incoming client
     * connections concurrently, improving scalability and efficiency.
     * </p>
     * <p>
     * Key operations performed within this method:
     * <ul>
     *     <li>Initialize a ServerSocket on a predefined port.</li>
     *     <li>Set up a ThreadPoolExecutor with specified core, maximum pool sizes, keep-alive time,
     *     and a work queue.</li>
     *     <li>Add a shutdown hook to ensure the executor service is gracefully shut down on
     *     application termination.</li>
     *     <li>Continuously listen for and accept incoming client connections, handing each
     *     off to the executor service.</li>
     * </ul>
     * </p>
     * <p>
     * Any IOException encountered during the server socket setup or client communication is
     * caught and wrapped in a RuntimeException.
     * </p>
     *
     * @param args command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(EchoServer.PORT_NUMBER)) {
            // Initialization and display of server details.
            System.out.println("Server started on: " +
                    "\nHostAddress: " + serverSocket.getInetAddress() +
                    "\nPortNumber: " + PORT_NUMBER);

            // ThreadPoolExecutor setup with specified parameters.
            int corePoolSize = 5;
            int maximumPoolSize = 50;
            long keepAliveTime = 60L;
            TimeUnit unit = TimeUnit.SECONDS;
            BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(1000);
            ThreadPoolExecutor executorService = new ThreadPoolExecutor(
                    corePoolSize,
                    maximumPoolSize,
                    keepAliveTime,
                    unit,
                    workQueue
            );

            // Adding a shutdown hook to gracefully shutdown the executor service.
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down executor...");
                executorService.shutdown();
                try {
                    if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                        executorService.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    executorService.shutdownNow();
                    e.printStackTrace();
                }
                System.out.println("Executor shutdown complete");
            }));

            // Continuously accept incoming client connections and handle using the executor service.
            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientSocket.setSoTimeout(EchoServer.SOCKET_TIMEOUT);
                executorService.execute(() -> handleClient(clientSocket));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Handles communication with a connected client.
     *
     * <p>
     * This method is responsible for:
     * <ul>
     *     <li>Setting up input and output streams for communication with the client.</li>
     *     <li>Receiving messages from the client and echoing them back. The messages are expected
     *     to be preceded by an integer indicating the message length.</li>
     *     <li>Ensuring that received messages are within valid length bounds.</li>
     *     <li>Gracefully handling various exceptions, such as socket exceptions or abrupt client disconnections.</li>
     * </ul>
     * </p>
     *
     * <p>
     * The communication protocol is as follows:
     * <ol>
     *     <li>Read an integer which indicates the length of the incoming message.</li>
     *     <li>If the length is invalid (either negative or exceeds the maximum allowed length),
     *     terminate the communication.</li>
     *     <li>Read the message bytes based on the received length.</li>
     *     <li>Convert the bytes to a string and display the received message.</li>
     *     <li>Echo the message back to the client.</li>
     * </ol>
     * </p>
     *
     * @param clientSocket The socket through which the server communicates with the connected client.
     */
    private static void handleClient(Socket clientSocket) {
        try (
                // Create output stream to send data to the client
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

                // Create input stream to receive data from the client
                DataInputStream in = new DataInputStream(clientSocket.getInputStream())
        ) {
            // Log the connection details for debugging purposes
            System.out.println("Server connected to client through: " +
                    "\nHostAddress: " + clientSocket.getInetAddress() +
                    "\nPortNumber: " + EchoServer.PORT_NUMBER);

            while (true) {
                // Receive the length of the incoming message
                int length = in.readInt();

                // Check the validity of the received message length
                if (length < 0 || length > EchoServer.MAX_MESSAGE_LENGTH) {
                    System.out.println("Invalid message length received");
                    return;
                }

                // Read the actual message based on the provided length
                byte[] bytes = new byte[length];
                in.readFully(bytes);

                // Convert the received bytes to a string for logging and processing
                String receivedMessage = new String(bytes, StandardCharsets.UTF_8);
                System.out.println("Received from client: " + receivedMessage);

                // Echo the received message back to the client
                out.writeInt(length);
                out.write(bytes);
            }
        } catch (SocketException e) {
            // Handle socket-related exceptions (e.g., connection reset or connection aborted)
            System.out.println("Socket error on port " + EchoServer.PORT_NUMBER);
            System.out.println(e.getMessage());
        } catch (EOFException e) {
            // Handle cases where the client disconnects abruptly without a proper closure
            System.out.println("Client disconnected abruptly.");
        } catch (IOException e) {
            // Handle generic I/O exceptions
            System.out.println("Exception caught when trying to communicate on port " + EchoServer.PORT_NUMBER);
            System.out.println(e.getMessage());
        }
    }

}

