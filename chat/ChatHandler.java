package chat;

import ui.ChatWindow;

import java.io.*;
import java.net.Socket;
import javax.swing.JFileChooser;

public class ChatHandler {
    private Socket socket;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;
    private ChatWindow chatWindow;

    public ChatHandler(Socket socket, ChatWindow chatWindow) throws IOException {
        this.socket = socket;
        this.chatWindow = chatWindow;

        dataIn = new DataInputStream(socket.getInputStream());
        dataOut = new DataOutputStream(socket.getOutputStream());

        // Luồng nhận dữ liệu
        new Thread(this::receiveLoop).start();
    }

    // Gửi tin nhắn text
    public void sendMessage(String message) {
        try {
            // Hiển thị trước khi gửi
            chatWindow.appendMessage("Me", message);

            dataOut.writeUTF("TEXT:" + message);
            dataOut.flush();
        } catch (IOException e) {
            chatWindow.appendMessage("System", "Gửi tin nhắn thất bại!");
        }
    }

    // Gửi file
    public void sendFile() {
        try {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(chatWindow) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                chatWindow.appendMessage("System", "Đang gửi file: " + file.getName());

                byte[] buffer = new byte[4096];
                FileInputStream fis = new FileInputStream(file);

                // Gửi header: FILE + tên file + độ dài
                dataOut.writeUTF("FILE:" + file.getName());
                dataOut.writeLong(file.length());

                int read;
                while ((read = fis.read(buffer)) != -1) {
                    dataOut.write(buffer, 0, read);
                }
                dataOut.flush();
                fis.close();

                chatWindow.appendMessage("System", "Đã gửi xong file: " + file.getName());
            }
        } catch (IOException e) {
            chatWindow.appendMessage("System", "Gửi file thất bại!");
        }
    }

    // Luồng nhận dữ liệu
    private void receiveLoop() {
        try {
            while (true) {
                String header = dataIn.readUTF();
                if (header.startsWith("TEXT:")) {
                    handleText(header.substring(5));
                } else if (header.startsWith("FILE:")) {
                    handleFile(header);
                }
            }
        } catch (IOException e) {
            chatWindow.appendMessage("System", "Kết nối bị đóng!");
        }
    }

    // Xử lý text
    private void handleText(String message) {
        chatWindow.appendMessage("Peer", message);
    }

    // Xử lý file
    private void handleFile(String header) {
        try {
            String fileName = header.substring(5);
            long fileLength = dataIn.readLong();

            // Hỏi người dùng nơi lưu file
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File(fileName));
            if (chooser.showSaveDialog(chatWindow) == JFileChooser.APPROVE_OPTION) {
                File saveFile = chooser.getSelectedFile();
                FileOutputStream fos = new FileOutputStream(saveFile);

                byte[] buffer = new byte[4096];
                long remaining = fileLength;
                int read;
                while (remaining > 0
                        && (read = dataIn.read(buffer, 0, (int) Math.min(buffer.length, remaining))) != -1) {
                    fos.write(buffer, 0, read);
                    remaining -= read;
                }
                fos.close();
                chatWindow.appendMessage("System", "Đã nhận file: " + saveFile.getName());
            } else {
                // Nếu user cancel, đọc bỏ dữ liệu
                byte[] buffer = new byte[4096];
                long remaining = fileLength;
                while (remaining > 0) {
                    int r = dataIn.read(buffer, 0, (int) Math.min(buffer.length, remaining));
                    if (r == -1)
                        break;
                    remaining -= r;
                }
                chatWindow.appendMessage("System", "Đã từ chối nhận file: " + fileName);
            }
        } catch (IOException e) {
            chatWindow.appendMessage("System", "Nhận file thất bại!");
        }
    }
}
