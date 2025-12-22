import java.net.*;
import java.io.*;
import java.util.*;

public class ChatServer {
    private static Set<ClientHandler> clientHandlers = new HashSet<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(12345);
        System.out.println("Сервер запущен на порту 12345");
        
        while (true) {
            Socket clientSocket = serverSocket.accept();
            ClientHandler client = new ClientHandler(clientSocket);
            clientHandlers.add(client);
            new Thread(client).start();
        }
    }
    
    static void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clientHandlers) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }
    
    static void removeClient(ClientHandler client) {
        clientHandlers.remove(client);
    }
}
