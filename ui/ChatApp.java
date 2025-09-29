package ui;

import socket.SocketConnector;
import chat.ChatHandler;

import javax.swing.*;

public class ChatApp {
    private static ChatHandler activeHandler; // handler chính để gửi/nhận

    public static void main(String[] args) {
        try {
            SocketConnector connector = new SocketConnector();
            ChatWindow chatWindow = new ChatWindow();

            // Luôn khởi chạy server
            connector.startServer(12345, chatWindow);

            // Hỏi có muốn kết nối tới peer khác không
            String host = JOptionPane.showInputDialog("Nhập IP của peer (bỏ trống nếu chỉ chờ):");
            if (host != null && !host.trim().isEmpty()) {
                connector.startClient(host.trim(), 12345, chatWindow);
            }

            // Luồng theo dõi incoming socket
            new Thread(() -> {
                while (true) {
                    try {
                        if (connector.getIncomingSocket() != null && activeHandler == null) {
                            activeHandler = new ChatHandler(connector.getIncomingSocket(), chatWindow);
                            attachActions(chatWindow, activeHandler);
                        }
                        Thread.sleep(500);
                    } catch (Exception ignored) {}
                }
            }).start();

            // Nếu đã có outgoing thì dùng luôn
            if (connector.getOutgoingSocket() != null) {
                activeHandler = new ChatHandler(connector.getOutgoingSocket(), chatWindow);
                attachActions(chatWindow, activeHandler);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi: " + e.getMessage());
        }
    }

    // Gắn hành động gửi tin nhắn / gửi file
    private static void attachActions(ChatWindow chatWindow, ChatHandler handler) {
        chatWindow.addSendAction(ev -> {
            String msg = chatWindow.getInputText();
            if (!msg.isEmpty()) {
                handler.sendMessage(msg);
                chatWindow.clearInput();
            }
        });
        chatWindow.addSendFileAction(ev -> handler.sendFile());
    }
}
