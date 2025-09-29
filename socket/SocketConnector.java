package socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import ui.ChatWindow;

public class SocketConnector {
    private Socket incomingSocket;  // Peer khác kết nối đến mình
    private Socket outgoingSocket;  // Mình kết nối đến peer khác

    // Server mode (chờ peer khác kết nối đến)
    public void startServer(int port, ChatWindow chatWindow) throws IOException {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                chatWindow.appendMessage("System", "Đang đợi peer kết nối...");
                incomingSocket = serverSocket.accept();
                chatWindow.appendMessage("System", "Peer đã kết nối đến!");
            } catch (IOException e) {
                chatWindow.appendMessage("System", "Server lỗi: " + e.getMessage());
            }
        }).start();
    }

    // Client mode (kết nối đến peer khác)
    public void startClient(String host, int port, ChatWindow chatWindow) throws IOException {
        outgoingSocket = new Socket(host, port);
        chatWindow.appendMessage("System", "Đã kết nối tới peer: " + host);
    }

    public Socket getIncomingSocket() {
        return incomingSocket;
    }

    public Socket getOutgoingSocket() {
        return outgoingSocket;
    }
}
