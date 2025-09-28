package ui;

import socket.SocketConnector;
import chat.ChatHandler;

public class ChatApp {
    public static void main(String[] args) {
        try {
            SocketConnector connector = new SocketConnector();
            ChatWindow chatWindow = new ChatWindow();

            try {
                // Nếu mở được cổng thì là server
                connector.startServer(12345, chatWindow);
            } catch (Exception e) {
                // Nếu không thì làm client
                connector.startClient("localhost", 12345, chatWindow);
            }

            // Sau khi kết nối xong thì tạo ChatHandler
            ChatHandler chatHandler = new ChatHandler(connector.getSocket(), chatWindow);

            // Gắn nút gửi tin nhắn text
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
