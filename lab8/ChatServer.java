// ChatServer.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.*;

public class ChatServer extends JFrame {
    private ServerSocket serverSocket;
    private boolean isRunning = false;
    private DefaultListModel<String> clientListModel;
    private JList<String> clientList;
    private JTextArea logArea;
    private JButton startButton, stopButton, kickButton;
    private JTextField portField;
    private Set<ClientHandler> clients;
    private int port = 12345;

    public ChatServer() {
        super("Chat Server");
        clients = Collections.synchronizedSet(new HashSet<>());
        initGUI();
    }

    private void initGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);

        // Панель управления
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(new JLabel("Port:"));
        portField = new JTextField("12345", 6);
        controlPanel.add(portField);

        startButton = new JButton("Start Server");
        stopButton = new JButton("Stop Server");
        kickButton = new JButton("Kick Selected");

        startButton.addActionListener(e -> startServer());
        stopButton.addActionListener(e -> stopServer());
        kickButton.addActionListener(e -> kickClient());

        stopButton.setEnabled(false);
        kickButton.setEnabled(false);

        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        controlPanel.add(kickButton);

        // Список клиентов
        clientListModel = new DefaultListModel<>();
        clientList = new JList<>(clientListModel);
        clientList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane clientScroll = new JScrollPane(clientList);
        clientScroll.setBorder(BorderFactory.createTitledBorder("Connected Clients"));

        // Лог сервера
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder("Server Log"));

        // Размещение компонентов
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, clientScroll, logScroll);
        splitPane.setDividerLocation(150);

        setLayout(new BorderLayout());
        add(controlPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private void startServer() {
        try {
            port = Integer.parseInt(portField.getText());
            serverSocket = new ServerSocket(port);
            isRunning = true;

            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            kickButton.setEnabled(true);
            portField.setEnabled(false);

            logMessage("Server started on port " + port);

            // Запускаем поток для принятия подключений
            new Thread(() -> {
                while (isRunning) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        ClientHandler client = new ClientHandler(clientSocket);
                        clients.add(client);
                        new Thread(client).start();
                    } catch (IOException e) {
                        if (isRunning) {
                            logMessage("Error accepting client: " + e.getMessage());
                        }
                    }
                }
            }).start();

        } catch (NumberFormatException e) {
            logMessage("Invalid port number!");
        } catch (IOException e) {
            logMessage("Failed to start server: " + e.getMessage());
        }
    }

    private void stopServer() {
        isRunning = false;

        // Отправляем сообщение о закрытии всем клиентам
        broadcast("SERVER:Server is shutting down", null);

        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            logMessage("Error closing server: " + e.getMessage());
        }

        // Закрываем все клиентские соединения
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.closeConnection();
            }
            clients.clear();
        }

        clientListModel.clear();
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        kickButton.setEnabled(false);
        portField.setEnabled(true);

        logMessage("Server stopped");
    }

    private void kickClient() {
        int selectedIndex = clientList.getSelectedIndex();
        if (selectedIndex != -1) {
            String clientInfo = clientListModel.get(selectedIndex);
            String clientId = clientInfo.split(" \\(")[0];

            synchronized (clients) {
                ClientHandler toRemove = null;
                for (ClientHandler client : clients) {
                    if (client.getName().equals(clientId)) {
                        toRemove = client;
                        break;
                    }
                }

                if (toRemove != null) {
                    toRemove.sendMessage("SERVER:You have been kicked by administrator");
                    toRemove.closeConnection();
                    clients.remove(toRemove);
                    updateClientList();
                    logMessage("Kicked client: " + clientId);
                }
            }
        }
    }

    void broadcast(String message, ClientHandler sender) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client != sender) {
                    client.sendMessage(message);
                }
            }
        }
    }

    void updateClientList() {
        SwingUtilities.invokeLater(() -> {
            clientListModel.clear();
            synchronized (clients) {
                for (ClientHandler client : clients) {
                    clientListModel.addElement(client.getName() + " (" +
                            client.getAddress() + ")");
                }
            }

            // Отправляем обновленный список всем клиентам
            StringBuilder userList = new StringBuilder("USERLIST:");
            synchronized (clients) {
                for (ClientHandler client : clients) {
                    userList.append(client.getName()).append(",");
                }
            }

            if (userList.length() > 9) {
                broadcast(userList.toString(), null);
            }
        });
    }

    void removeClient(ClientHandler client) {
        clients.remove(client);
        updateClientList();
        logMessage("Client disconnected: " + client.getName());
    }

    void logMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(new Date() + ": " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    // Класс для обработки клиента
    class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String name;
        private String address;

        public ClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            this.address = socket.getInetAddress().getHostAddress();
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        @Override
        public void run() {
            try {
                // Получаем имя клиента
                String firstMessage = in.readLine();
                if (firstMessage != null && firstMessage.startsWith("NAME:")) {
                    name = firstMessage.substring(5);
                    if (name.length() > 20) name = name.substring(0, 20);

                    logMessage("New client connected: " + name + " from " + address);

                    // Приветственное сообщение
                    sendMessage("SERVER:Welcome to the chat, " + name + "!");
                    sendMessage("SERVER:There are " + (clients.size() - 1) + " other users online");

                    // Уведомляем всех о новом пользователе
                    broadcast("SERVER:" + name + " has joined the chat", this);

                    updateClientList();

                    // Обработка сообщений от клиента
                    String message;
                    while ((message = in.readLine()) != null) {
                        if (message.equals("QUIT")) {
                            break;
                        } else if (message.startsWith("MSG:")) {
                            String content = message.substring(4);
                            String formattedMessage = name + ": " + content;

                            // Логируем на сервере
                            logMessage(formattedMessage);

                            // Рассылаем всем
                            broadcast(formattedMessage, this);
                        } else if (message.equals("GET_USERS")) {
                            updateClientList();
                        }
                    }
                }
            } catch (IOException e) {
                logMessage("Error with client " + name + ": " + e.getMessage());
            } finally {
                closeConnection();
                removeClient(this);
                if (name != null) {
                    broadcast("SERVER:" + name + " has left the chat", null);
                }
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }

        public void closeConnection() {
            try {
                if (out != null) out.close();
                if (in != null) in.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                logMessage("Error closing connection for " + name);
            }
        }

        public String getName() {
            return name != null ? name : "Unknown";
        }

        public String getAddress() {
            return address;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatServer());
    }
}
