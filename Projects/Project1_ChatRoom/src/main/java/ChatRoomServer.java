package Projects.Project1_ChatRoom.src.main.java;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ChatRoomServer {
    /**
     * The port number on which the server listens.
     */
    private static final int PORT_NUMBER = 5555;
    /**
     * Maximum allowed message length in bytes.
     */
    private static final int MAX_MESSAGE_LENGTH = 10_000;
    /**
     * Socket timeout in milliseconds.
     */
    private static final int SOCKET_TIMEOUT = 60_000;
    private static final int N_CHAT_HISTORY_MESSAGES = 10;
    private static final LinkedList<String> chatHistory = new LinkedList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(ChatRoomServer.PORT_NUMBER)) {
            // Initialization and display of server details.
            System.out.println("Server started, waiting for client:" +
                    " HostAddress: " + serverSocket.getInetAddress() +
                    " PortNumber: " + PORT_NUMBER);

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
                clientSocket.setSoTimeout(ChatRoomServer.SOCKET_TIMEOUT);
                executorService.execute(() -> handleClient(clientSocket));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
                // Create output stream to send data to the client
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

                // Create input stream to receive data from the client
                DataInputStream in = new DataInputStream(clientSocket.getInputStream())
        ) {
            // Log the connection details for debugging purposes
            System.out.println("Server connected to client through:" +
                    " HostAddress: " + clientSocket.getInetAddress().getHostAddress() +
                    " PortNumber: " + ChatRoomServer.PORT_NUMBER
            );


            // wait for username and print it
            handleIncomingMessage(in);

            // Sends the chat history message
            out.writeInt(("Recent Chat History:\n" + getChat()).getBytes(StandardCharsets.UTF_8).length);
            out.write(("Recent Chat History:\n" + getChat()).getBytes(StandardCharsets.UTF_8));


            while (true) {
                handleIncomingMessage(in);
            }
        } catch (SocketException e) {
            // Handle socket-related exceptions (e.g., connection reset or connection aborted)
            System.out.println("Socket error on port " + ChatRoomServer.PORT_NUMBER);
            System.out.println("Message: " + e.getMessage());
        } catch (EOFException e) {
            // Handle cases where the client disconnects abruptly without a proper closure
            System.out.println("Client disconnected abruptly.");
        } catch (IOException e) {
            // Handle generic I/O exceptions
            System.out.println("Exception caught when trying to communicate on port " + ChatRoomServer.PORT_NUMBER);
            System.out.println("Message: " + e.getMessage());
        }
    }

    private synchronized static void updateChatHistory(String message) {
        if (chatHistory.size() >= N_CHAT_HISTORY_MESSAGES) {
            chatHistory.removeFirst();
        }
        chatHistory.addLast(message + "\n");
    }

    private synchronized static String getChat() {
        StringBuilder chat = new StringBuilder();
        for (String message : chatHistory) {
            chat.append(message);
        }
        return chat.toString();
    }

    private static void handleIncomingMessage(DataInputStream in) throws IOException {
        // Receive the length of the incoming message
        int length = in.readInt();

        // Check the validity of the received message length
        if (length < 0 || length > ChatRoomServer.MAX_MESSAGE_LENGTH) {
            System.out.println("Invalid message length received");
            return;
        }

        // Read the actual message based on the provided length
        byte[] bytes = new byte[length];
        in.readFully(bytes);

        // Convert the received bytes to a string for logging and processing
        String receivedMessage = new String(bytes, StandardCharsets.UTF_8);
        System.out.println(receivedMessage);
        updateChatHistory(receivedMessage);
    }

}