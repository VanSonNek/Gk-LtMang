package ui;

import socket.SocketConnector;
import chat.ChatHandler;

import javax.swing.*;

public class ChatApp {
    public static void main(String[] args) {
        try {
            SocketConnector connector = new SocketConnector();
            ChatWindow chatWindow = new ChatWindow();

            // Luôn chạy server ở cổng 12345
            connector.startServer(12345, chatWindow);

            // Hỏi có muốn kết nối tới peer khác không
            String host = JOptionPane.showInputDialog("Nhập IP của peer (bỏ trống nếu chỉ chờ):");
            if (host != null && !host.trim().isEmpty()) {
                connector.startClient(host.trim(), 12345, chatWindow);
            }

            // Tạo ChatHandler cho cả incoming và outgoing
            new Thread(() -> {
                while (true) {
                    try {
                        if (connector.getIncomingSocket() != null) {
                            new ChatHandler(connector.getIncomingSocket(), chatWindow);
                            break;
                        }
                        Thread.sleep(500);
                    } catch (Exception ignored) {}
                }
            }).start();

            if (connector.getOutgoingSocket() != null) {
                new ChatHandler(connector.getOutgoingSocket(), chatWindow);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
