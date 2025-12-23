import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class Client extends JFrame {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean connected = false;

    // GUI компоненты
    private JTextArea chatArea;
    private JTextField messageField;
    private JTextField serverField;
    private JTextField portField;
    private JTextField nameField;
    private JButton connectButton;
    private JButton disconnectButton;
    private JButton sendButton;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private JLabel statusLabel;

    public Client() {
        super("Client");
        initGUI();
    }

    private void initGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        // Верхняя панель подключения и статуса
        JPanel topPanel = new JPanel(new BorderLayout());

        // Панель подключения
        JPanel connectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        connectPanel.add(new JLabel("Server:"));
        serverField = new JTextField("localhost", 10);
        connectPanel.add(serverField);

        connectPanel.add(new JLabel("Port:"));
        portField = new JTextField("12345", 5);
        connectPanel.add(portField);

        connectPanel.add(new JLabel("Your Name:"));
        nameField = new JTextField("User" + (int)(Math.random()*1000), 10);
        connectPanel.add(nameField);

        connectButton = new JButton("Connect");
        disconnectButton = new JButton("Disconnect");
        disconnectButton.setEnabled(false);

        connectButton.addActionListener(e -> connectToServer());
        disconnectButton.addActionListener(e -> disconnectFromServer());

        connectPanel.add(connectButton);
        connectPanel.add(disconnectButton);

        // Статус
        statusLabel = new JLabel("Status: Disconnected");
        statusLabel.setForeground(Color.RED);

        topPanel.add(connectPanel, BorderLayout.WEST);
        topPanel.add(statusLabel, BorderLayout.EAST);

        // Центральная область: чат и список пользователей
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.setBorder(BorderFactory.createTitledBorder("Chat Messages"));

        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane userScroll = new JScrollPane(userList);
        userScroll.setBorder(BorderFactory.createTitledBorder("Online Users"));
        userScroll.setPreferredSize(new Dimension(150, 0));

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                chatScroll,
                userScroll
        );
        splitPane.setDividerLocation(600);

        // Нижняя панель отправки сообщений
        JPanel messagePanel = new JPanel(new BorderLayout(5, 5));
        messagePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        messageField = new JTextField();
        messageField.addActionListener(e -> sendMessage());
        messageField.setEnabled(false);

        sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendMessage());
        sendButton.setEnabled(false);

        messagePanel.add(new JLabel("Message: "), BorderLayout.WEST);
        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);

        // Меню
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem connectItem = new JMenuItem("Connect");
        JMenuItem disconnectItem = new JMenuItem("Disconnect");
        JMenuItem exitItem = new JMenuItem("Exit");

        connectItem.addActionListener(e -> connectToServer());
        disconnectItem.addActionListener(e -> disconnectFromServer());
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(connectItem);
        fileMenu.add(disconnectItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        // Добавляем всё на форму
        setJMenuBar(menuBar);
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(messagePanel, BorderLayout.SOUTH);

        // Обработка закрытия окна
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnectFromServer();
            }
        });

        setVisible(true);
    }

    private void connectToServer() {
        if (connected) {
            showError("Already connected!");
            return;
        }

        try {
            String server = serverField.getText().trim();
            int port = Integer.parseInt(portField.getText().trim());
            String name = nameField.getText().trim();

            if (name.isEmpty()) {
                showError("Please enter your name!");
                return;
            }

            if (name.length() > 20) {
                name = name.substring(0, 20);
                nameField.setText(name);
            }

            // Подключаемся к серверу
            socket = new Socket(server, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Отправляем имя серверу
            out.println("NAME:" + name);

            // Запускаем поток для приёма сообщений
            new Thread(new MessageReceiver()).start();

            // Обновляем интерфейс
            connected = true;
            connectButton.setEnabled(false);
            disconnectButton.setEnabled(true);
            sendButton.setEnabled(true);
            messageField.setEnabled(true);
            serverField.setEnabled(false);
            portField.setEnabled(false);
            nameField.setEnabled(false);
            messageField.requestFocus();

            statusLabel.setText("Status: Connected to " + server + ":" + port);
            statusLabel.setForeground(Color.GREEN);

            chatArea.append("=== Connected to chat server ===\n");

        } catch (NumberFormatException e) {
            showError("Invalid port number!");
        } catch (UnknownHostException e) {
            showError("Server not found: " + e.getMessage());
        } catch (IOException e) {
            showError("Connection error: " + e.getMessage());
        }
    }

    private void disconnectFromServer() {
        if (!connected) return;

        try {
            // Отправляем команду отключения
            if (out != null) {
                out.println("QUIT");
            }

            // Закрываем соединения
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();

            // Обновляем интерфейс
            connected = false;
            connectButton.setEnabled(true);
            disconnectButton.setEnabled(false);
            sendButton.setEnabled(false);
            messageField.setEnabled(false);
            serverField.setEnabled(true);
            portField.setEnabled(true);
            nameField.setEnabled(true);

            statusLabel.setText("Status: Disconnected");
            statusLabel.setForeground(Color.RED);

            userListModel.clear();
            chatArea.append("=== Disconnected from server ===\n\n");

        } catch (IOException e) {
            showError("Error while disconnecting: " + e.getMessage());
        }
    }

    private void sendMessage() {
        if (!connected) {
            showError("Not connected to server!");
            return;
        }

        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            out.println("MSG:" + message);
            messageField.setText("");
            messageField.requestFocus();
        }
    }

    // Класс для приёма сообщений в отдельном потоке
    private class MessageReceiver implements Runnable {
        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    final String msg = message;

                    SwingUtilities.invokeLater(() -> processMessage(msg));
                }
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    if (connected) {
                        showError("Connection lost: " + e.getMessage());
                        disconnectFromServer();
                    }
                });
            }
        }
    }

    private void processMessage(String message) {
        if (message.startsWith("USERLIST:")) {
            // Обновляем список пользователей
            String users = message.substring(9);
            userListModel.clear();
            String[] userArray = users.split(",");
            for (String user : userArray) {
                if (!user.isEmpty()) {
                    userListModel.addElement(user);
                }
            }
        }
        else if (message.startsWith("SERVER:")) {
            // Сообщение от сервера
            String serverMsg = message.substring(7);
            chatArea.append("[Server] " + serverMsg + "\n");

            // Если нас кикнули
            if (serverMsg.contains("kicked") || serverMsg.contains("shutting down")) {
                SwingUtilities.invokeLater(() -> {
                    chatArea.append("=== Connection terminated ===\n\n");
                    disconnectFromServer();
                });
            }
        }
        else {
            // Обычное сообщение от пользователя
            chatArea.append(message + "\n");
        }

        // Автопрокрутка
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Client());
    }
}
