package socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import ui.ChatWindow;

public class SocketConnector {
    private Socket socket;

    // Server mode
    public void startServer(int port, ChatWindow chatWindow) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        chatWindow.appendMessage("System", "Đang đợi client kết nối...");
        socket = serverSocket.accept(); // chờ client
        chatWindow.appendMessage("System", "Client đã kết nối thành công!");
    }

    // Client mode
    public void startClient(String host, int port, ChatWindow chatWindow) throws IOException {
        socket = new Socket(host, port);
        chatWindow.appendMessage("System", "Đã kết nối tới server!");
    }

    public Socket getSocket() {
        return socket;
    }
}
