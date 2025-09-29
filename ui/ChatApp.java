package ui;

import socket.SocketConnector;
import chat.ChatHandler;

import javax.swing.*;

public class ChatApp {
    public static void main(String[] args) {
        try {
            SocketConnector connector = new SocketConnector();
            ChatWindow chatWindow = new ChatWindow();

            String[] options = {"Server", "Client"};
            int choice = JOptionPane.showOptionDialog(
                    null,
                    "Bạn muốn chạy chế độ nào?",
                    "Chọn chế độ",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice == 0) {
                // Làm server
                connector.startServer(12345, chatWindow);
            } else {
                // Làm client → nhập IP server
                String host = JOptionPane.showInputDialog("Nhập IP của server:");
                connector.startClient(host, 12345, chatWindow);
            }

            // Sau khi kết nối thành công → tạo handler
            ChatHandler chatHandler = new ChatHandler(connector.getSocket(), chatWindow);

            // Gắn nút gửi text
            chatWindow.addSendAction(ev -> {
                String msg = chatWindow.getInputText();
                if (!msg.isEmpty()) {
                    chatHandler.sendMessage(msg);
                    chatWindow.clearInput();
                }
            });

            // Gắn nút gửi file
            chatWindow.addSendFileAction(ev -> chatHandler.sendFile());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
