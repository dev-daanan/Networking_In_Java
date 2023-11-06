package Projects.Project2_SharedResources.src.main.java;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResourceServer {
    private static final int PORT_NUMBER = 5555;
    private static final ArrayList<String> projectTodos = new ArrayList<>();

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        System.out.println("Resource Server is running on port " + PORT_NUMBER);
        try (ServerSocket serverSocket = new ServerSocket(PORT_NUMBER)) {
            while (true) {
                Socket clientSocket = serverSocket.accept(); // Wait and accept a connection
                executorService.execute(() -> handleClient(clientSocket)); // Handle the client connection
            }
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        addProjectTodo("Testing");
        System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress());

        try (
                DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream())
        ) {
            while (true) {

                String optionsMessage = "What would you like to do:\n1. Print all todos\n2. Add a todo\n3. Remove a todo";
                // send optionsMessage to ask for option
                NetworkUtils.sendMessage(out, optionsMessage);
                // Receive chosenOption
                String chosenOption = NetworkUtils.readMessage(in);
                int choice = 0;
                if (chosenOption.length() == 1) {
                    choice = Integer.parseInt(chosenOption);
                    if (choice > 0 && choice < 4) {
                        switch (choice) {
                            case 1 -> {
                                NetworkUtils.sendMessage(out, getProjectTodos());
                                continue;
                            }
                            case 2 -> NetworkUtils.sendMessage(out, "Please enter the todo to add.");
                            case 3 -> NetworkUtils.sendMessage(out, "Please enter the index to delete:\n" + getProjectTodos());
                        }
                    }
                }
                switch (choice) {
                    case 2 -> addProjectTodo(NetworkUtils.readMessage(in));
                    case 3 -> deleteProjectTodo(Integer.parseInt(NetworkUtils.readMessage(in)));
                }
            }
        } catch (EOFException e) {
            System.out.println("Client " + clientSocket.getRemoteSocketAddress() + " closed connection.");
        } catch (IOException e) {
            System.err.println("IOException with client " + clientSocket.getRemoteSocketAddress() + ": " + e.getMessage());
        } finally {
            try {
                clientSocket.close(); // Always close the client socket after handling
            } catch (IOException e) {
                System.err.println("Could not close client socket: " + e.getMessage());
            }
        }
    }


    private synchronized static void addProjectTodo(String todo) {
        if (projectTodos.contains(todo)) {
            System.out.println(todo + " is already in the todo list.");
        } else {
            projectTodos.add(todo);
            System.out.println(todo + " has been added to the todo list.");
        }
    }

    private synchronized static String getProjectTodos() {
        StringBuilder todos = new StringBuilder();
        for (int i = 0; i < projectTodos.size(); i++) {
            String todo = projectTodos.get(i);
            todos.append(i + 1).append(". ").append(todo).append("\n");
        }
        return todos.toString();
    }

    private synchronized static String deleteProjectTodo(int index) {
        if (index <= 0 || index >= projectTodos.size()) {
            return "";
        } else {
            return projectTodos.remove(index - 1);
        }
    }

}

